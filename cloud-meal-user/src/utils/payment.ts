import type { PaymentCreate } from '@/types'
import { request } from '@/utils/request'

export async function payOrder(orderId:string) {
  const payment=await request<PaymentCreate>({url:`/user/orders/${orderId}/payment`,method:'POST'})
  if(payment.status==='PAID'){
    uni.showToast({title:payment.mode==='FREE'?'优惠券已全额抵扣':'本地支付成功'})
    return true
  }

  // #ifndef MP-WEIXIN
  uni.showModal({title:'请在微信小程序付款',content:'真实微信支付只能在微信开发者工具或手机微信小程序中唤起。',showCancel:false})
  return false
  // #endif

  // #ifdef MP-WEIXIN
  try{
    await uni.requestPayment({
      provider:'wxpay',
      timeStamp:payment.timeStamp!,
      nonceStr:payment.nonceStr!,
      package:payment.packageValue!,
      signType:payment.signType as 'MD5'|'HMAC-SHA256'|'RSA',
      paySign:payment.paySign!
    })
    await request<void>({url:`/user/orders/${orderId}/payment/confirm`,method:'POST'})
    uni.showToast({title:'支付成功'})
    return true
  }catch(error:any){
    if(String(error?.errMsg||'').includes('cancel')) uni.showToast({title:'已取消支付',icon:'none'})
    else uni.showToast({title:'支付结果确认失败，请刷新订单',icon:'none'})
    return false
  }
  // #endif
}
