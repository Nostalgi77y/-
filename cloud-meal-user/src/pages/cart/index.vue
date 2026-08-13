<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { request } from '@/utils/request'
import type { Address, Order, UserCoupon } from '@/types'

const cart=useCartStore();const user=useUserStore();const addresses=ref<Address[]>([]);const coupons=ref<UserCoupon[]>([])
const selectedAddress=ref<Address>();const selectedCoupon=ref<UserCoupon>();const showAddresses=ref(false);const showCoupons=ref(false)
const discount=computed(()=>selectedCoupon.value?.discountAmount||0);const payable=computed(()=>Math.max(0,cart.totalPrice-discount.value))

async function loadCheckout(){
  if(!user.isLoggedIn) await user.demoLogin()
  await cart.load()
  addresses.value=await request<Address[]>({url:'/user/addresses',method:'GET'})
  selectedAddress.value=addresses.value.find(item=>item.isDefault===1)||addresses.value[0]
  coupons.value=await request<UserCoupon[]>({url:`/user/coupons/mine?orderAmount=${cart.totalPrice}`,method:'GET'})
  if(selectedCoupon.value&&!coupons.value.find(item=>item.userCouponId===selectedCoupon.value?.userCouponId&&item.usable)) selectedCoupon.value=undefined
}
function chooseAddress(item:Address){selectedAddress.value=item;showAddresses.value=false}
function chooseCoupon(item?:UserCoupon){selectedCoupon.value=item;showCoupons.value=false}
async function checkout(){
  if(!cart.items.length)return
  if(!selectedAddress.value){uni.showModal({title:'缺少收货地址',content:'请先新增收货地址',success:r=>{if(r.confirm)uni.navigateTo({url:'/pages/address/index'})}});return}
  const order=await request<Order>({url:'/user/orders',method:'POST',data:{clientOrderNo:`uni-${Date.now()}`,addressBookId:selectedAddress.value.id,userCouponId:selectedCoupon.value?.userCouponId,remark:''}})
  uni.showModal({title:'订单创建成功',content:`商品 ¥${order.originalAmount}，优惠 ¥${order.discountAmount}，应付 ¥${order.amount}。是否模拟支付？`,success:async result=>{if(result.confirm){await request<void>({url:`/user/orders/${order.id}/mock-pay`,method:'POST'});uni.switchTab({url:'/pages/order/index'})}}})
}
onShow(loadCheckout)
</script>
<template><view class="cart-page">
  <view class="heading"><text>购物车</text><text class="clear" @click="cart.clear">清空</text></view>
  <view v-if="!cart.items.length" class="empty">购物车还是空的<text>去挑选喜欢的菜品吧</text></view>
  <view v-for="item in cart.items" :key="item.id" class="item"><view class="thumb">膳</view><view class="item-main"><text class="name">{{item.dishName}}</text><text class="price">¥{{item.unitPrice}}</text></view><text>× {{item.quantity}}</text></view>
  <view v-if="cart.items.length" class="options"><view @click="showAddresses=true"><text>收货地址</text><text class="value">{{selectedAddress?selectedAddress.consignee+' · '+selectedAddress.detail:'请选择'}} ›</text></view><view @click="showCoupons=true"><text>优惠券</text><text class="value discount">{{selectedCoupon?'- ¥'+selectedCoupon.discountAmount:(coupons.filter(i=>i.usable).length+'张可用')}} ›</text></view></view>
  <view v-if="cart.items.length" class="settle"><view><text class="saved" v-if="discount">已优惠 ¥{{discount.toFixed(2)}}</text><text>合计</text><text class="total">¥{{payable.toFixed(2)}}</text></view><button @click="checkout">提交订单</button></view>

  <view v-if="showAddresses" class="mask" @click.self="showAddresses=false"><view class="sheet"><text class="title">选择收货地址</text><view v-for="item in addresses" :key="item.id" class="choice" @click="chooseAddress(item)"><view><text>{{item.consignee}}　{{item.phone}}</text><small>{{item.province}}{{item.city}}{{item.district}}{{item.detail}}</small></view><text v-if="selectedAddress?.id===item.id">✓</text></view><button class="manage" @click="showAddresses=false;uni.navigateTo({url:'/pages/address/index'})">管理收货地址</button></view></view>
  <view v-if="showCoupons" class="mask" @click.self="showCoupons=false"><view class="sheet"><text class="title">选择优惠券</text><view class="choice" @click="chooseCoupon(undefined)"><text>不使用优惠券</text><text v-if="!selectedCoupon">✓</text></view><view v-for="item in coupons" :key="item.userCouponId" class="choice" :class="{disabled:!item.usable}" @click="item.usable&&chooseCoupon(item)"><view><text>{{item.name}}　减¥{{item.discountAmount}}</text><small>满¥{{item.thresholdAmount}}可用</small></view><text v-if="selectedCoupon?.userCouponId===item.userCouponId">✓</text></view></view></view>
</view></template>
<style scoped lang="scss">.cart-page{padding:30rpx 30rpx 190rpx}.heading{display:flex;justify-content:space-between;font-size:38rpx;font-weight:700;margin:20rpx 0 30rpx}.clear{font-size:24rpx;font-weight:400;color:#888}.empty{text-align:center;color:#65716b;padding:180rpx 0}.empty text{display:block;font-size:24rpx;color:#a0a8a4;margin-top:16rpx}.item{display:flex;align-items:center;background:#fff;padding:24rpx;border-radius:24rpx;margin-bottom:18rpx}.thumb{width:110rpx;height:110rpx;border-radius:20rpx;background:#dfe9e3;display:grid;place-items:center;color:#315c4c}.item-main{flex:1;padding:0 22rpx}.name,.price{display:block}.name{font-weight:700}.price{color:#d64d37;margin-top:16rpx}.options{background:#fff;border-radius:24rpx;padding:0 24rpx;margin-top:20rpx}.options>view{display:flex;justify-content:space-between;padding:28rpx 0;border-bottom:1rpx solid #edf0ee}.value{max-width:430rpx;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;color:#65716b}.discount{color:#d64d37}.settle{position:fixed;left:0;right:0;bottom:100rpx;background:#fff;padding:24rpx 30rpx;display:flex;align-items:center;justify-content:space-between;z-index:10}.saved{display:block;font-size:20rpx;color:#d64d37}.total{font-size:36rpx;font-weight:700;color:#d64d37;margin-left:16rpx}.settle button,.manage{margin:0;background:#123c2f;color:#fff;border-radius:30rpx;padding:0 46rpx}.mask{position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:flex-end;z-index:30}.sheet{width:100%;max-height:70vh;overflow:auto;background:#fff;border-radius:34rpx 34rpx 0 0;padding:34rpx;box-sizing:border-box}.title{display:block;font-size:34rpx;font-weight:700;margin-bottom:24rpx}.choice{display:flex;justify-content:space-between;align-items:center;padding:24rpx 0;border-bottom:1rpx solid #edf0ee}.choice small{display:block;color:#87918c;margin-top:8rpx}.disabled{opacity:.45}.manage{width:100%;margin-top:26rpx}</style>
