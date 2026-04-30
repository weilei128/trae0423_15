import request from '@/utils/request'

export function checkIn(appointmentNo) {
  return request.post('/checkin', null, { params: { appointmentNo } })
}

export function startConsultation(appointmentId) {
  return request.post(`/checkin/${appointmentId}/start`)
}

export function completeConsultation(appointmentId) {
  return request.post(`/checkin/${appointmentId}/complete`)
}

export function markNoShow(appointmentId) {
  return request.post(`/checkin/${appointmentId}/no-show`)
}
