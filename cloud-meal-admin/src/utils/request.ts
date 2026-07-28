import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type { ApiResponse } from '@/types/api'

const http = axios.create({ baseURL: '/api', timeout: 10000 })
http.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
http.interceptors.response.use(
  response => {
    const result = response.data as ApiResponse<unknown>
    if (result.code !== 'SUCCESS') return Promise.reject(new Error(result.message))
    return response
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      router.push('/login')
    }
    ElMessage.error(error.response?.data?.message || error.message || '请求失败')
    return Promise.reject(error)
  },
)
export default http
