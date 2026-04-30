import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const getDefaultRoute = (userRoles) => {
  if (userRoles.includes('ADMIN')) {
    return '/dashboard'
  } else if (userRoles.includes('DOCTOR')) {
    return '/doctor-queue'
  } else if (userRoles.includes('PATIENT')) {
    return '/appointment'
  } else if (userRoles.includes('REGISTRAR')) {
    return '/check-in'
  }
  return '/check-in'
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        redirect: to => {
          const userStore = useUserStore()
          const userRoles = userStore.user?.roles?.map(r => r.name) || []
          return getDefaultRoute(userRoles)
        }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', roles: ['ADMIN'] }
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/Appointment.vue'),
        meta: { title: '预约挂号', roles: ['PATIENT'] }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: () => import('@/views/MyAppointments.vue'),
        meta: { title: '我的预约', roles: ['PATIENT'] }
      },
      {
        path: 'check-in',
        name: 'CheckIn',
        component: () => import('@/views/CheckIn.vue'),
        meta: { title: '就诊签到', roles: ['PATIENT', 'REGISTRAR'] }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: () => import('@/views/Schedule.vue'),
        meta: { title: '排班管理', roles: ['ADMIN'] }
      },
      {
        path: 'doctor-queue',
        name: 'DoctorQueue',
        component: () => import('@/views/DoctorQueue.vue'),
        meta: { title: '候诊队列', roles: ['DOCTOR'] }
      },
      {
        path: 'medical-records',
        name: 'MedicalRecords',
        component: () => import('@/views/MedicalRecords.vue'),
        meta: { title: '病历记录', roles: ['PATIENT', 'DOCTOR'] }
      },
      {
        path: 'examinations',
        name: 'Examinations',
        component: () => import('@/views/Examinations.vue'),
        meta: { title: '检查申请', roles: ['PATIENT', 'DOCTOR'] }
      },
      {
        path: 'payments',
        name: 'Payments',
        component: () => import('@/views/Payments.vue'),
        meta: { title: '费用缴纳', roles: ['PATIENT'] }
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 医院门诊预约挂号系统` : '医院门诊预约挂号系统'
  
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  
  if (!token) {
    next('/login')
    return
  }
  
  if (!userStore.user && token) {
    try {
      await userStore.getCurrentUser()
    } catch (error) {
      localStorage.removeItem('token')
      next('/login')
      return
    }
  }
  
  if (to.meta.roles && to.meta.roles.length > 0) {
    const userRoles = userStore.user?.roles?.map(r => r.name) || []
    const hasRole = to.meta.roles.some(role => userRoles.includes(role))
    
    if (!hasRole) {
      const defaultRoute = getDefaultRoute(userRoles)
      if (defaultRoute !== to.path) {
        next(defaultRoute)
        return
      }
    }
  }
  
  next()
})

export default router
