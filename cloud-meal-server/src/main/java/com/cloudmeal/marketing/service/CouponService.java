package com.cloudmeal.marketing.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.marketing.entity.Coupon;
import com.cloudmeal.marketing.entity.UserCoupon;
import com.cloudmeal.marketing.mapper.CouponMapper;
import com.cloudmeal.marketing.mapper.UserCouponMapper;
import com.cloudmeal.marketing.vo.UserCouponVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    public CouponService(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper; this.userCouponMapper = userCouponMapper;
    }

    public List<Coupon> available() {
        LocalDateTime now = LocalDateTime.now();
        return couponMapper.selectList(Wrappers.<Coupon>lambdaQuery().eq(Coupon::getStatus, 1)
                .le(Coupon::getValidFrom, now).ge(Coupon::getValidUntil, now)
                .apply("received_count < total_count").orderByAsc(Coupon::getThresholdAmount));
    }

    @Transactional
    public void receive(Long couponId) {
        Long userId = CurrentUser.id();
        if (userCouponMapper.selectCount(Wrappers.<UserCoupon>lambdaQuery()
                .eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, couponId)) > 0) {
            throw new BusinessException("COUPON_ALREADY_RECEIVED", "该优惠券已经领取");
        }
        Coupon coupon = couponMapper.selectById(couponId);
        LocalDateTime now = LocalDateTime.now();
        if (coupon == null || coupon.getStatus() != 1 || now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())
                || couponMapper.takeOne(couponId) != 1) {
            throw new BusinessException("COUPON_UNAVAILABLE", "优惠券不可领取或已领完");
        }
        UserCoupon owned = new UserCoupon(); owned.setUserId(userId); owned.setCouponId(couponId);
        owned.setStatus("UNUSED"); owned.setReceivedTime(now); userCouponMapper.insert(owned);
    }

    public List<UserCouponVO> mine(BigDecimal orderAmount) {
        Long userId = CurrentUser.id();
        return userCouponMapper.selectList(Wrappers.<UserCoupon>lambdaQuery().eq(UserCoupon::getUserId, userId)
                .orderByDesc(UserCoupon::getReceivedTime)).stream().map(owned -> {
            Coupon coupon = couponMapper.selectById(owned.getCouponId());
            boolean usable = coupon != null && "UNUSED".equals(owned.getStatus()) && coupon.getStatus() == 1
                    && LocalDateTime.now().isBefore(coupon.getValidUntil())
                    && (orderAmount == null || orderAmount.compareTo(coupon.getThresholdAmount()) >= 0);
            return new UserCouponVO(owned.getId(), owned.getCouponId(), coupon.getName(), coupon.getThresholdAmount(),
                    coupon.getDiscountAmount(), coupon.getValidUntil(), owned.getStatus(), usable);
        }).toList();
    }

    public Coupon validateForOrder(Long userCouponId, Long userId, BigDecimal originalAmount) {
        if (userCouponId == null) return null;
        UserCoupon owned = userCouponMapper.selectOne(Wrappers.<UserCoupon>lambdaQuery()
                .eq(UserCoupon::getId, userCouponId).eq(UserCoupon::getUserId, userId).eq(UserCoupon::getStatus, "UNUSED"));
        if (owned == null) throw new BusinessException("COUPON_INVALID", "优惠券不存在或已使用");
        Coupon coupon = couponMapper.selectById(owned.getCouponId());
        LocalDateTime now = LocalDateTime.now();
        if (coupon == null || coupon.getStatus() != 1 || now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())
                || originalAmount.compareTo(coupon.getThresholdAmount()) < 0) {
            throw new BusinessException("COUPON_INVALID", "优惠券不满足使用条件");
        }
        return coupon;
    }

    public void markUsed(Long userCouponId, Long userId, Long orderId) {
        if (userCouponId != null && userCouponMapper.markUsed(userCouponId, userId, orderId) != 1) {
            throw new BusinessException("COUPON_INVALID", "优惠券使用失败");
        }
    }
}
