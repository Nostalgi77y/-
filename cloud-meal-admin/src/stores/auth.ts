import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import http from '@/utils/request'
import type { ApiResponse } from '@/types/api'

interface LoginData { token: string; userId: number; name: string; role: string }
export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const name = ref(localStorage.getItem('admin_name') || '')
  const loggedIn = computed(() => Boolean(token.value))
  async function login(username: string, password: string) {
    const { data } = await http.post<ApiResponse<LoginData>>('/auth/login', { username, password })
    token.value = data.data.token; name.value = data.data.name
    localStorage.setItem('admin_token', token.value); localStorage.setItem('admin_name', name.value)
  }
  function logout() { token.value = ''; name.value = ''; localStorage.removeItem('admin_token'); localStorage.removeItem('admin_name') }
  return { token, name, loggedIn, login, logout }
})
