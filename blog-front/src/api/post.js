import { get, post } from '@/utils/request'
import request from '@/utils/request'

/**
 * 分页获取文章列表
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页条数
 * @returns {Promise} 请求结果
 */
export function getPostList(pageNum, pageSize) {
  return get('/post/page', {
    pageNum,
    pageSize
  })
}

/**
 * 发表评论
 * @param {Object} commentData 评论数据
 * @returns {Promise} 请求结果
 */
export function makeComment(data) {
  return request({
    url: '/post/comment',
    method: 'post',
    data
  })
}

/**
 * 分页获取朋友圈列表
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页条数
 * @returns {Promise} 请求结果
 */
export function getFriendsPosts(params) {
  return get('/post/page', params)
} 