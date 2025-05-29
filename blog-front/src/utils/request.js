import axios from 'axios'
import { ElMessage } from 'element-plus'
import { API_BASE_URL } from '@/config'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  // 允许携带cookie
  withCredentials: true,
  // 允许获取响应头
  exposedHeaders: ['Authorization']
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 确保headers对象存在
    if (!config.headers) {
      config.headers = {}
    }

    // 设置通用headers
    config.headers['Content-Type'] = 'application/json'
    config.headers['Accept'] = 'application/json'

    // 获取并设置token（如果存在）
    const token = localStorage.getItem('token')
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    return config
  },
  error => {
    ElMessage.error('请求配置错误')
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 保存响应头中的token（如果有的话）
    const authHeader = response.headers?.authorization
    if (authHeader) {
      const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7) : authHeader
      localStorage.setItem('token', token)
    }

    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 处理特定的错误码
      if (res.code === 401) {
        // token过期或无效
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求错误，未找到该资源')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查您的网络连接')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

// 封装GET请求
export function get(url, params) {
  return request({
    url,
    method: 'get',
    params
  })
}

// 封装POST请求
export function post(url, data) {
  return request({
    url,
    method: 'post',
    data
  })
}

// 封装PUT请求
export function put(url, data) {
  return request({
    url,
    method: 'put',
    data
  })
}

// 封装DELETE请求
export function del(url, params) {
  return request({
    url,
    method: 'delete',
    params
  })
}

export default request
