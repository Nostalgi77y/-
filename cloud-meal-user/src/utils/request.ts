import type { ApiResponse } from '@/types'

// 本地构建默认连接电脑上的 Docker 后端；发布前替换为备案的 HTTPS 域名。
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export function resolveAssetUrl(url?: string) {
  if (!url || /^(https?:|data:|blob:)/i.test(url)) return url || ''
  const origin = API_BASE_URL.replace(/\/api\/?$/, '')
  return url.startsWith('/') ? origin + url : `${origin}/${url}`
}

export function request<T>(options: UniApp.RequestOptions) {
  return new Promise<T>((resolve, reject) => {
    const token = uni.getStorageSync('user_token')
    uni.request({
      ...options,
      url: API_BASE_URL + options.url,
      header: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.header },
      success(response) {
        if (response.statusCode === 401) {
          uni.removeStorageSync('user_token')
          uni.showToast({ title: '请先登录', icon: 'none' })
          reject(new Error('请先登录'))
          return
        }
        const result = response.data as ApiResponse<T>
        if (!result || result.code !== 'SUCCESS') {
          const message = result?.message || `请求失败（${response.statusCode}）`
          uni.showToast({ title: message, icon: 'none' })
          reject(new Error(message))
          return
        }
        resolve(result.data)
      },
      fail(error) {
        uni.showToast({ title: '网络连接失败，请检查后端服务', icon: 'none' })
        reject(error)
      },
    })
  })
}
