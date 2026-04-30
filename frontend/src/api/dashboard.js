import request from '@/utils/request'

export function getDailyStatistics(date) {
  return request.get(`/dashboard/daily/${date}`)
}

export function getTodayStatistics() {
  return request.get('/dashboard/today')
}

export function getDepartmentLoad(date) {
  return request.get(`/dashboard/department-load/${date}`)
}

export function getRevenueStatistics(startDate, endDate) {
  return request.get('/dashboard/revenue', { params: { startDate, endDate } })
}
