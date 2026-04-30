import request from '@/utils/request'

export function createPayment(payment) {
  return request.post('/payments', payment)
}

export function getPaymentById(id) {
  return request.get(`/payments/${id}`)
}

export function getPaymentByNo(paymentNo) {
  return request.get(`/payments/no/${paymentNo}`)
}

export function getMyPayments() {
  return request.get('/payments/my')
}

export function processPayment(id, paymentMethod, transactionId) {
  return request.post(`/payments/${id}/pay`, { paymentMethod, transactionId })
}

export function cancelPayment(id) {
  return request.post(`/payments/${id}/cancel`)
}
