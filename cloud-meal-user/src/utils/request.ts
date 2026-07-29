import type { ApiResponse } from '@/types'

// 本地构建默认连接电脑上的 Docker 后端；发布前通过 VITE_API_BASE_URL 替换为备案的 HTTPS 域名。
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

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
