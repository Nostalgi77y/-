<script setup lang="ts">
import { useUserStore } from '@/stores/user'
const user=useUserStore()
async function ensureLogin(){ if(!user.isLoggedIn) await user.login() }
async function go(url:string){ await ensureLogin(); uni.navigateTo({url}) }
</script>
<template>
  <view class="mine">
    <view class="profile"><view class="avatar">膳</view><view><text class="name">{{user.name}}</text><text class="sub">{{user.isLoggedIn?'已登录':'登录后享受完整服务'}}</text></view><button v-if="!user.isLoggedIn" @click="user.login">微信登录</button></view>
    <view class="panel">
      <view @click="go('/pages/address/index')">收货地址<text>›</text></view>
      <view @click="go('/pages/coupon/index')">我的优惠券<text>›</text></view>
      <view>联系客服<text>›</text></view>
      <view v-if="user.isLoggedIn" class="logout" @click="user.logout">退出登录<text>›</text></view>
    </view>
  </view>
</template>
<style scoped lang="scss">.mine{padding:30rpx}.profile{margin-top:30rpx;background:#123c2f;color:#fff;padding:42rpx 30rpx;border-radius:34rpx;display:flex;align-items:center}.avatar{width:100rpx;height:100rpx;border-radius:30rpx;background:#e5bd5a;color:#123c2f;display:grid;place-items:center;font-size:40rpx;font-weight:700;margin-right:24rpx}.name,.sub{display:block}.name{font-size:34rpx;font-weight:700}.sub{color:#b8d0c6;font-size:22rpx;margin-top:10rpx}.profile button{margin-left:auto;margin-right:0;font-size:24rpx;background:#e5bd5a;color:#123c2f}.panel{background:#fff;border-radius:26rpx;margin-top:28rpx;padding:0 28rpx}.panel view{display:flex;justify-content:space-between;padding:30rpx 0;border-bottom:1rpx solid #edf0ee}.panel text{color:#9ca49f}.logout{color:#d44f3a}</style>
