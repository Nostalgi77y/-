import type { ApiResponse } from '@/types'

const BASE_URL = import.meta.env.DEV ? 'http://localhost:8080/api' : 'https://api.example.com/api'

export function request<T>(options: UniApp.RequestOptions) {
  return new Promise<T>((resolve, reject) => {
    const token = uni.getStorageSync('user_token')
    uni.request({
      ...options, url: BASE_URL + options.url,
      header: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.header },
      success(response) {
        if (response.statusCode === 401) {
          uni.removeStorageSync('user_token'); reject(new Error('请先登录')); return
        }
        const result = response.data as ApiResponse<T>
        if (result.code !== 'SUCCESS') {
          uni.showToast({ title: result.message || '请求失败', icon: 'none' }); reject(new Error(result.message)); return
        }
        resolve(result.data)
      },
      fail(error) { uni.showToast({ title: '网络连接失败', icon: 'none' }); reject(error) },
    })
  })
}
