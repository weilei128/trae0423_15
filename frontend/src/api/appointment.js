import request from '@/utils/request'

export function createAppointment(scheduleId) {
  return request.post('/appointments', scheduleId)
}

export function getAppointmentById(id) {
  return request.get(`/appointments/${id}`)
}

export function getAppointmentByNo(appointmentNo) {
  return request.get(`/appointments/no/${appointmentNo}`)
}

export function getMyAppointments() {
  return request.get('/appointments/my')
}

export function getDoctorAppointmentsByDate(doctorId, date) {
  return request.get(`/appointments/doctor/${doctorId}/date/${date}`)
}

export function cancelAppointment(id) {
  return request.post(`/appointments/${id}/cancel`)
}
