import { get } from '@/utils/request'

/**
 * 分页获取技术博客文档列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页条数
 * @param {number} [params.categoryId] 分类ID，可选
 * @returns {Promise} 请求结果
 */
export function getDocPage(params) {
  return get('/api/doc/page', params)
}

/**
 * 根据ID获取文档详情
 * @param {number} id 文档ID
 * @returns {Promise} 请求结果
 */
export function getDocById(id) {
  return get(`/api/doc/${id}`)
}

/**
 * 获取文档分类列表
 * @returns {Promise} 请求结果，包含分类列表
 */
export function getDocCategories() {
  return get('/api/doc/category')
}

/**
 * 获取最近文档列表
 * @returns {Promise} 请求结果
 */
export function getRecentDocs() {
  return get('/api/doc/recently')
} 