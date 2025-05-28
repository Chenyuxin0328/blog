import { post } from '@/utils/request'

/**
 * 用户登录
 * @param {string} username 用户名
 * @param {string} password 密码
 * @returns {Promise} 请求结果，包含token和用户信息
 */
export function login(username, password) {
  return post('/auth/login', {
    username,
    password
  })
}

/**
 * 保存用户登录信息
 * @param {Object} data 登录返回的数据，包含token和用户信息
 */
export function saveLoginInfo(data) {
  // 保存token和用户信息到localStorage
  localStorage.setItem('token', data.token)
  localStorage.setItem('userInfo', JSON.stringify(data.user))
}

/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息，未登录返回null
 */
export function getCurrentUser() {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : null
}

/**
 * 退出登录
 */
export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  // 跳转到登录页
  window.location.href = '/'
} 