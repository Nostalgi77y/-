<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request'
import type { Order } from '@/types'
const orders=ref<Order[]>([]);const labels:Record<string,string>={PENDING_PAYMENT:'待付款',PENDING_ACCEPTANCE:'商家待接单',PREPARING:'制作中',PENDING_DELIVERY:'待配送',DELIVERING:'配送中',COMPLETED:'已完成',CANCELLED:'已取消'}
onShow(async()=>{try{orders.value=await request<Order[]>({url:'/user/orders',method:'GET'})}catch{orders.value=[]}})
</script>
<template><view class="orders"><view v-if="!orders.length" class="empty">暂无订单</view><view v-for="order in orders" :key="order.id" class="order"><view class="order-head"><text>订单 {{order.orderNumber}}</text><text class="status">{{labels[order.status]}}</text></view><view v-for="detail in order.details" :key="detail.id" class="detail"><text>{{detail.name}} × {{detail.quantity}}</text><text>¥{{detail.amount}}</text></view><view v-if="order.discountAmount" class="discount">优惠券抵扣 -¥{{order.discountAmount}}</view><view class="order-foot"><text>{{order.createdTime?.replace('T',' ')}}</text><text class="amount">实付 ¥{{order.amount}}</text></view></view></view></template>
<style scoped lang="scss">.orders{padding:24rpx}.empty{text-align:center;color:#89928e;padding-top:240rpx}.order{background:#fff;border-radius:24rpx;padding:26rpx;margin-bottom:20rpx}.order-head,.detail,.order-foot{display:flex;justify-content:space-between}.order-head{font-weight:700;padding-bottom:20rpx;border-bottom:1rpx solid #edf0ee}.status{color:#19704e}.detail{padding-top:20rpx;color:#56615c}.discount{text-align:right;color:#d64d37;font-size:22rpx;margin-top:16rpx}.order-foot{margin-top:24rpx;color:#929a96;font-size:22rpx}.amount{color:#1d2923;font-size:28rpx;font-weight:700}</style>
