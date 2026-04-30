import request from '@/utils/request'

export function createExamination(examination) {
  return request.post('/examinations', examination)
}

export function getExaminationById(id) {
  return request.get(`/examinations/${id}`)
}

export function getExaminationByNo(examNo) {
  return request.get(`/examinations/no/${examNo}`)
}

export function getMyExaminations() {
  return request.get('/examinations/my')
}

export function getExaminationsByMedicalRecord(medicalRecordId) {
  return request.get(`/examinations/medical-record/${medicalRecordId}`)
}

export function updateExaminationStatus(id, status) {
  return request.put(`/examinations/${id}/status`, null, { params: { status } })
}

export function updateExamResult(id, examResult, reportUrl) {
  return request.put(`/examinations/${id}/result`, { examResult, reportUrl })
}
