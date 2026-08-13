<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request'
import type { Coupon, UserCoupon } from '@/types'

const available = ref<Coupon[]>([])
const mine = ref<UserCoupon[]>([])
const ownedCouponIds = computed(() => new Set(mine.value.map(item => item.couponId)))

async function load() {
  ;[available.value, mine.value] = await Promise.all([
    request<Coupon[]>({ url: '/user/coupons/available', method: 'GET' }),
    request<UserCoupon[]>({ url: '/user/coupons/mine', method: 'GET' })
  ])
}

async function receive(id: string) {
  if (ownedCouponIds.value.has(id)) return
  await request<void>({ url: `/user/coupons/${id}/receive`, method: 'POST' })
  uni.showToast({ title: '领取成功' })
  await load()
}

onShow(load)
</script>

<template>
  <view class="page">
    <text class="section">可领取</text>
    <view v-for="item in available" :key="item.id" class="coupon">
      <view class="money"><text>¥{{ item.discountAmount }}</text><small>满 ¥{{ item.thresholdAmount }} 可用</small></view>
      <view class="info"><text>{{ item.name }}</text><small>有效期至 {{ item.validUntil?.slice(0, 10) }}</small></view>
      <button :disabled="ownedCouponIds.has(item.id)" @click="receive(item.id)">
        {{ ownedCouponIds.has(item.id) ? '已领取' : '领取' }}
      </button>
    </view>

    <text class="section">我的优惠券</text>
    <view v-for="item in mine" :key="item.userCouponId" class="coupon mine" :class="{ disabled: item.status !== 'UNUSED' }">
      <view class="money"><text>¥{{ item.discountAmount }}</text><small>满 ¥{{ item.thresholdAmount }} 可用</small></view>
      <view class="info">
        <text>{{ item.name }}</text>
        <small>{{ item.status === 'USED' ? '已使用' : `有效期至 ${item.validUntil?.slice(0, 10)}` }}</small>
      </view>
    </view>
    <view v-if="!mine.length" class="empty">还没有领取优惠券</view>
  </view>
</template>

<style scoped lang="scss">
.page{padding:24rpx}.section{display:block;font-size:30rpx;font-weight:700;margin:24rpx 4rpx 18rpx}.coupon{display:flex;align-items:center;background:#fff;border-radius:24rpx;padding:24rpx;margin-bottom:18rpx;border-left:8rpx solid #e5bd5a}.money{width:190rpx;color:#d64d37}.money text,.money small,.info text,.info small{display:block}.money text{font-size:42rpx;font-weight:700}.money small,.info small{font-size:20rpx;color:#8b948f;margin-top:8rpx}.info{flex:1}.info text{font-weight:700}.coupon button{margin:0;background:#123c2f;color:#fff;font-size:24rpx;border-radius:24rpx}.coupon button[disabled]{background:#a8afac;color:#fff}.disabled{opacity:.55;filter:grayscale(1)}.empty{text-align:center;color:#999;padding:80rpx}
</style>
