import request from '@/utils/request'

export function createMedicalRecord(medicalRecord) {
  return request.post('/medical-records', medicalRecord)
}

export function getMedicalRecordById(id) {
  return request.get(`/medical-records/${id}`)
}

export function getMedicalRecordByAppointment(appointmentId) {
  return request.get(`/medical-records/appointment/${appointmentId}`)
}

export function getMyMedicalRecords() {
  return request.get('/medical-records/my')
}

export function updateMedicalRecord(id, medicalRecord) {
  return request.put(`/medical-records/${id}`, medicalRecord)
}
