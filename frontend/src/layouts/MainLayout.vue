<template>
  <el-container class="main-layout">
    <el-header class="header">
      <div class="header-left">
        <el-icon class="logo-icon"><Hospital /></el-icon>
        <span class="title">医院门诊预约挂号系统</span>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userStore.user?.realName || '用户' }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-container>
      <el-aside width="220px" class="aside">
        <el-menu
          :default-active="activeMenu"
          router
          class="side-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item v-if="userStore.isAdmin" index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient" index="/appointment">
            <el-icon><Calendar /></el-icon>
            <span>预约挂号</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient" index="/my-appointments">
            <el-icon><List /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient || userStore.isRegistrar" index="/check-in">
            <el-icon><CircleCheck /></el-icon>
            <span>就诊签到</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isAdmin" index="/schedule">
            <el-icon><Clock /></el-icon>
            <span>排班管理</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isDoctor" index="/doctor-queue">
            <el-icon><UserFilled /></el-icon>
            <span>候诊队列</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient || userStore.isDoctor" index="/medical-records">
            <el-icon><Document /></el-icon>
            <span>病历记录</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient || userStore.isDoctor" index="/examinations">
            <el-icon><Microscope /></el-icon>
            <span>检查申请</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isPatient" index="/payments">
            <el-icon><Wallet /></el-icon>
            <span>费用缴纳</span>
          </el-menu-item>
          
          <el-menu-item v-if="userStore.isAdmin" index="/user-management">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 28px;
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 16px;
}

.aside {
  background-color: #304156;
}

.side-menu {
  border-right: none;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
