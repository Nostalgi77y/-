package com.cloudmeal.order.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.cart.entity.ShoppingCart;
import com.cloudmeal.cart.mapper.ShoppingCartMapper;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.order.dto.OrderSubmitRequest;
import com.cloudmeal.order.entity.Order;
import com.cloudmeal.order.entity.OrderDetail;
import com.cloudmeal.order.mapper.OrderDetailMapper;
import com.cloudmeal.order.mapper.OrderMapper;
import com.cloudmeal.order.messaging.OrderMessageConfig;
import com.cloudmeal.order.model.OrderStatus;
import com.cloudmeal.order.vo.OrderVO;
import com.cloudmeal.notification.OrderNotificationHandler;
import com.cloudmeal.product.mapper.DishMapper;
import com.cloudmeal.product.entity.Dish;
import com.cloudmeal.user.entity.AddressBook;
import com.cloudmeal.user.mapper.AddressBookMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper detailMapper;
    private final ShoppingCartMapper cartMapper;
    private final DishMapper dishMapper;
    private final AddressBookMapper addressMapper;
    private final RabbitTemplate rabbitTemplate;
    private final OrderNotificationHandler notifier;

    public OrderService(OrderMapper orderMapper, OrderDetailMapper detailMapper, ShoppingCartMapper cartMapper,
                        DishMapper dishMapper, AddressBookMapper addressMapper, RabbitTemplate rabbitTemplate,
                        OrderNotificationHandler notifier) {
        this.orderMapper = orderMapper; this.detailMapper = detailMapper; this.cartMapper = cartMapper;
        this.dishMapper = dishMapper; this.addressMapper = addressMapper; this.rabbitTemplate = rabbitTemplate;
        this.notifier = notifier;
    }

    @Transactional
    public OrderVO submit(OrderSubmitRequest request) {
        Long userId = CurrentUser.id();
        Order duplicate = orderMapper.selectOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getUserId, userId).eq(Order::getClientOrderNo, request.clientOrderNo()));
        if (duplicate != null) return getForUser(duplicate.getId(), userId);

        AddressBook address = addressMapper.selectOne(Wrappers.<AddressBook>lambdaQuery()
                .eq(AddressBook::getId, request.addressBookId()).eq(AddressBook::getUserId, userId));
        if (address == null) throw new BusinessException("ADDRESS_NOT_FOUND", "收货地址不存在");
        List<ShoppingCart> carts = cartMapper.selectList(Wrappers.<ShoppingCart>lambdaQuery().eq(ShoppingCart::getUserId, userId));
        if (carts.isEmpty()) throw new BusinessException("CART_EMPTY", "购物车为空");

        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Dish> currentDishes = new HashMap<>();
        for (ShoppingCart cart : carts) {
            Dish currentDish = dishMapper.selectById(cart.getDishId());
            if (currentDish == null || currentDish.getStatus() != 1) {
                throw new BusinessException("DISH_UNAVAILABLE", cart.getDishName() + "已下架");
            }
            if (dishMapper.deductStock(cart.getDishId(), cart.getQuantity()) != 1) {
                throw new BusinessException("STOCK_NOT_ENOUGH", cart.getDishName() + "库存不足或已下架");
            }
            currentDishes.put(cart.getDishId(), currentDish);
            total = total.add(currentDish.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber()); order.setClientOrderNo(request.clientOrderNo());
        order.setUserId(userId); order.setAddressBookId(address.getId());
        order.setStatus(OrderStatus.PENDING_PAYMENT.name()); order.setPayStatus("UNPAID");
        order.setAmount(total); order.setConsignee(address.getConsignee()); order.setPhone(address.getPhone());
        order.setAddress(address.fullAddress()); order.setRemark(request.remark()); order.setVersion(0);
        orderMapper.insert(order);

        for (ShoppingCart cart : carts) {
            Dish currentDish = currentDishes.get(cart.getDishId());
            OrderDetail detail = new OrderDetail(); detail.setOrderId(order.getId()); detail.setDishId(cart.getDishId());
            detail.setName(currentDish.getName()); detail.setImage(currentDish.getImage()); detail.setUnitPrice(currentDish.getPrice());
            detail.setQuantity(cart.getQuantity());
            detail.setAmount(currentDish.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            detail.setCreatedTime(LocalDateTime.now()); detailMapper.insert(detail);
        }
        cartMapper.delete(Wrappers.<ShoppingCart>lambdaQuery().eq(ShoppingCart::getUserId, userId));
        rabbitTemplate.convertAndSend(OrderMessageConfig.DELAY_EXCHANGE, "delay", order.getId().toString());
        notifier.broadcast("ORDER_CREATED", order.getId(), "收到新订单");
        return toVO(order, details(order.getId()));
    }

    public List<OrderVO> userOrders() {
        Long userId = CurrentUser.id();
        return orderMapper.selectList(Wrappers.<Order>lambdaQuery().eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreatedTime)).stream()
                .map(o -> toVO(o, details(o.getId()))).toList();
    }

    public List<OrderVO> adminOrders() {
        return orderMapper.selectList(Wrappers.<Order>lambdaQuery().orderByDesc(Order::getCreatedTime)).stream()
                .map(o -> toVO(o, details(o.getId()))).toList();
    }

    public OrderVO getForUser(Long id, Long userId) {
        Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery().eq(Order::getId, id).eq(Order::getUserId, userId));
        if (order == null) throw new BusinessException("ORDER_NOT_FOUND", "订单不存在");
        return toVO(order, details(id));
    }

    @Transactional
    public void mockPay(Long id) {
        Long userId = CurrentUser.id();
        Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery().eq(Order::getId, id).eq(Order::getUserId, userId));
        if (order == null) throw new BusinessException("ORDER_NOT_FOUND", "订单不存在");
        if (orderMapper.markPaid(id, userId) != 1) {
            throw new BusinessException("ORDER_STATUS_INVALID", "当前订单不能支付");
        }
        notifier.broadcast("ORDER_PAID", id, "订单已支付，请及时接单");
    }

    @Transactional
    public void transition(Long id, OrderStatus target) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("ORDER_NOT_FOUND", "订单不存在");
        OrderStatus current = OrderStatus.valueOf(order.getStatus());
        if (!current.canTransitionTo(target) || orderMapper.transition(id, current.name(), target.name()) != 1) {
            throw new BusinessException("ORDER_STATUS_INVALID", "不允许执行该状态变更");
        }
        notifier.broadcast("ORDER_STATUS_CHANGED", id, "订单状态已更新为 " + target.name());
    }

    @Transactional
    public void closeIfUnpaid(Long id) {
        if (orderMapper.transition(id, OrderStatus.PENDING_PAYMENT.name(), OrderStatus.CANCELLED.name()) == 1) {
            for (OrderDetail detail : details(id)) {
                dishMapper.restoreStock(detail.getDishId(), detail.getQuantity());
            }
        }
    }

    private List<OrderDetail> details(Long orderId) {
        return detailMapper.selectList(Wrappers.<OrderDetail>lambdaQuery().eq(OrderDetail::getOrderId, orderId));
    }
    private OrderVO toVO(Order order, List<OrderDetail> details) {
        return new OrderVO(order.getId(), order.getOrderNumber(), order.getStatus(), order.getPayStatus(), order.getAmount(),
                order.getConsignee(), order.getPhone(), order.getAddress(), order.getRemark(), order.getCreatedTime(), details);
    }
    private String generateOrderNumber() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(100, 1000);
    }
}
