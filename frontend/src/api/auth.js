import request from '@/utils/request'

export function login(username, password) {
  return request.post('/auth/login', { username, password })
}

export function register(userData) {
  return request.post('/auth/register', userData)
}

export function getCurrentUser() {
  return request.get('/auth/me')
}
