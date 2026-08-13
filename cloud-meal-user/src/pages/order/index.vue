<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request'
import { payOrder } from '@/utils/payment'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const deletingId = ref<string>()
const payingId = ref<string>()
const labels: Record<string, string> = {
  PENDING_PAYMENT: '待付款', PENDING_ACCEPTANCE: '商家待接单', PREPARING: '制作中',
  PENDING_DELIVERY: '待配送', DELIVERING: '配送中', COMPLETED: '已完成', CANCELLED: '已取消'
}

function canDelete(order: Order) {
  return order.status === 'COMPLETED' || order.status === 'CANCELLED'
}

async function pay(order:Order){
  payingId.value=order.id
  try{await payOrder(order.id);await load()}finally{payingId.value=undefined}
}

async function load() {
  try { orders.value = await request<Order[]>({ url: '/user/orders', method: 'GET' }) }
  catch { orders.value = [] }
}

function remove(order: Order) {
  uni.showModal({
    title: '删除订单记录',
    content: `确认删除订单 ${order.orderNumber} 吗？删除后不会在用户端显示。`,
    confirmText: '删除',
    confirmColor: '#d64d37',
    success: async result => {
      if (!result.confirm) return
      deletingId.value = order.id
      try {
        await request<void>({ url: `/user/orders/${order.id}`, method: 'DELETE' })
        orders.value = orders.value.filter(item => item.id !== order.id)
        uni.showToast({ title: '订单已删除' })
      } finally { deletingId.value = undefined }
    }
  })
}

onShow(load)
</script>

<template>
  <view class="orders">
    <view v-if="!orders.length" class="empty">暂无订单</view>
    <view v-for="order in orders" :key="order.id" class="order">
      <view class="order-head"><text>订单 {{ order.orderNumber }}</text><text class="status">{{ labels[order.status] }}</text></view>
      <view class="goods-title">购买商品</view>
      <view v-for="detail in order.details" :key="detail.id" class="detail"><text>{{ detail.name }} × {{ detail.quantity }}</text><text>¥{{ detail.amount }}</text></view>
      <view v-if="order.discountAmount" class="discount">优惠券抵扣 -¥{{ order.discountAmount }}</view>
      <view class="order-foot"><text>{{ order.createdTime?.replace('T', ' ') }}</text><text class="amount">实付 ¥{{ order.amount }}</text></view>
      <view v-if="order.status==='PENDING_PAYMENT'||canDelete(order)" class="actions">
        <button v-if="order.status==='PENDING_PAYMENT'" class="pay" :loading="payingId===order.id" :disabled="!!payingId" @click="pay(order)">立即付款</button>
        <button v-if="canDelete(order)" class="delete" :loading="deletingId===order.id" :disabled="!!deletingId" @click="remove(order)">删除记录</button>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.orders{padding:24rpx}.empty{text-align:center;color:#89928e;padding-top:240rpx}.order{background:#fff;border-radius:24rpx;padding:26rpx;margin-bottom:20rpx}.order-head,.detail,.order-foot{display:flex;justify-content:space-between}.order-head{font-weight:700;padding-bottom:20rpx;border-bottom:1rpx solid #edf0ee}.status{color:#19704e}.goods-title{font-size:22rpx;color:#929a96;margin-top:20rpx}.detail{padding-top:16rpx;color:#56615c}.discount{text-align:right;color:#d64d37;font-size:22rpx;margin-top:16rpx}.order-foot{margin-top:24rpx;color:#929a96;font-size:22rpx}.amount{color:#1d2923;font-size:28rpx;font-weight:700}.actions{display:flex;justify-content:flex-end;border-top:1rpx solid #edf0ee;margin-top:22rpx;padding-top:18rpx}.delete{margin:0;padding:0 28rpx;height:58rpx;line-height:58rpx;border-radius:30rpx;background:#fff3f1;color:#c84332;font-size:24rpx}
.pay{margin:0 16rpx 0 0;padding:0 32rpx;height:58rpx;line-height:58rpx;border-radius:30rpx;background:#123c2f;color:#fff;font-size:24rpx}
</style>
