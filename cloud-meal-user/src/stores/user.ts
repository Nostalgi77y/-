import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { request } from '@/utils/request'

interface LoginResult { token:string; userId:number; name:string; role:string }
export const useUserStore = defineStore('user', () => {
  const token = ref(''); const name = ref('微信用户'); const isLoggedIn = computed(() => Boolean(token.value))
  function restore(){ token.value=uni.getStorageSync('user_token')||''; name.value=uni.getStorageSync('user_name')||'微信用户' }
  async function demoLogin(){ const result=await request<LoginResult>({url:'/user/auth/demo-login',method:'POST'}); token.value=result.token; name.value=result.name; uni.setStorageSync('user_token',token.value); uni.setStorageSync('user_name',name.value) }
  function logout(){ token.value=''; name.value='微信用户'; uni.removeStorageSync('user_token'); uni.removeStorageSync('user_name') }
  return { token,name,isLoggedIn,restore,demoLogin,logout }
})
