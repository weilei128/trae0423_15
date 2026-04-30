import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getCurrentUser, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  
  const userRoles = computed(() => {
    return user.value?.roles?.map(r => r.name) || []
  })

  const hasRole = (role) => {
    return userRoles.value.includes(role)
  }

  const isAdmin = computed(() => hasRole('ADMIN'))
  const isDoctor = computed(() => hasRole('DOCTOR'))
  const isPatient = computed(() => hasRole('PATIENT'))
  const isRegistrar = computed(() => hasRole('REGISTRAR'))

  async function doLogin(username, password) {
    try {
      const response = await login(username, password)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      return response
    } catch (error) {
      throw error
    }
  }

  async function doRegister(userData) {
    try {
      const response = await register(userData)
      return response
    } catch (error) {
      throw error
    }
  }

  async function getCurrentUserInfo() {
    try {
      const response = await getCurrentUser()
      user.value = response
      return response
    } catch (error) {
      throw error
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    user,
    isLoggedIn,
    userRoles,
    hasRole,
    isAdmin,
    isDoctor,
    isPatient,
    isRegistrar,
    doLogin,
    doRegister,
    getCurrentUser: getCurrentUserInfo,
    logout
  }
})
