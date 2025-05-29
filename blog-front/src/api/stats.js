import { get } from '@/utils/request'

/**
 * 获取周统计数据
 */
export function getWeekStats() {
  return get('/api/weeklystats/week')
}

/**
 * 获取总体统计数据
 */
export function getTotalStats() {
  return get('/api/weeklystats/total')
}

/**
 * 获取访问统计
 */
export function getVisitCount() {
  return get('/api/weeklystats/visitcount')
} 