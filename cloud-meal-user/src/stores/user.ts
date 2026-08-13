import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { request } from '@/utils/request'

interface LoginResult { token:string; userId:number; name:string; role:string }
export const useUserStore = defineStore('user', () => {
  const token = ref(''); const name = ref('微信用户'); const isLoggedIn = computed(() => Boolean(token.value))
  function restore(){
    token.value=uni.getStorageSync('user_token')||''; name.value=uni.getStorageSync('user_name')||'微信用户'
    // #ifdef MP-WEIXIN
    if(token.value&&uni.getStorageSync('user_auth_provider')!=='wechat') logout()
    // #endif
  }
  function save(result:LoginResult,provider:'wechat'|'demo'){ token.value=result.token; name.value=result.name; uni.setStorageSync('user_token',token.value); uni.setStorageSync('user_name',name.value); uni.setStorageSync('user_auth_provider',provider) }
  async function demoLogin(){ save(await request<LoginResult>({url:'/user/auth/demo-login',method:'POST'}),'demo') }
  async function login(){
    // #ifdef MP-WEIXIN
    const loginResult=await uni.login({provider:'weixin'})
    save(await request<LoginResult>({url:'/user/auth/wechat-login',method:'POST',data:{code:loginResult.code}}),'wechat')
    return undefined
    // #endif
    await demoLogin()
  }
  function logout(){ token.value=''; name.value='微信用户'; uni.removeStorageSync('user_token'); uni.removeStorageSync('user_name'); uni.removeStorageSync('user_auth_provider') }
  return { token,name,isLoggedIn,restore,login,demoLogin,logout }
})
