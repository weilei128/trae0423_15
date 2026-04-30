import request from '@/utils/request'

export function getDepartments() {
  return request.get('/public/departments')
}

export function getDoctorsByDepartment(departmentId) {
  return request.get(`/public/departments/${departmentId}/doctors`)
}

export function getSchedulesByDate(date) {
  return request.get(`/public/schedules/date/${date}`)
}

export function getSchedulesByDepartmentAndDate(departmentId, date) {
  return request.get(`/public/schedules/department/${departmentId}/date/${date}`)
}

export function getSchedulesByDoctorAndDate(doctorId, date) {
  return request.get(`/public/schedules/doctor/${doctorId}/date/${date}`)
}

export function createSchedule(schedule) {
  return request.post('/schedules', schedule)
}

export function getScheduleById(id) {
  return request.get(`/schedules/${id}`)
}

export function updateSchedule(id, schedule) {
  return request.put(`/schedules/${id}`, schedule)
}

export function deleteSchedule(id) {
  return request.delete(`/schedules/${id}`)
}
