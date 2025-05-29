import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { API_BASE_URL } from '@/config'
import { ElMessage } from 'element-plus'

// 创建专门用于登录的axios实例
const loginRequest = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  withCredentials: true
})

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  console.log('Stored token:', token.value)
  
  const storedUserInfo = localStorage.getItem('userInfo')
  console.log('Raw stored user info:', storedUserInfo)
  const userInfo = ref(JSON.parse(storedUserInfo || '{}'))
  console.log('Parsed user info:', userInfo.value)

  // 计算登录状态
  const isLoggedIn = computed(() => {
    return !!token.value && token.value !== 'undefined' && token.value !== 'null'
  })

  // 设置token和用户信息
  const setUserInfo = (authToken, user) => {
    console.log('Setting user info:', { authToken, user })
    if (authToken) {
      token.value = authToken
      localStorage.setItem('token', authToken)
    }

    if (user) {
      userInfo.value = user
      localStorage.setItem('userInfo', JSON.stringify(user))
    }
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 登录
  const login = async (username, password) => {
    try {
      const response = await loginRequest.post('/api/auth/login', {
        username,
        password
      }, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      })

      const token = response.headers['authorization']
      if (!token) {
        console.error('Response headers:', response.headers)
        throw new Error('登录响应头中没有找到token')
      }

      setUserInfo(token.replace('Bearer ', ''), response.data.data)
      return response.data
    } catch (error) {
      clearUserInfo()
      throw error
    }
  }

  // 登出
  const logout = async () => {
    try {
      await loginRequest.post('/api/auth/logout', null, {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      })
    } catch (error) {
      console.error('登出时发生错误:', error)
    } finally {
      clearUserInfo()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    setUserInfo,
    clearUserInfo
  }
}) 