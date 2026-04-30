<template>
  <div class="user-management-page">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>
      
      <el-table
        :data="users"
        style="width: 100%"
        empty-text="暂无用户数据"
        v-loading="loading"
      >
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="200" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" width="180">
          <template #default="scope">
            {{ scope.row.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="角色" width="150">
          <template #default="scope">
            <el-tag
              v-for="role in scope.row.roles"
              :key="role.id"
              :type="getRoleTagType(role.name)"
              size="small"
              style="margin-right: 4px"
            >
              {{ getRoleText(role.name) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
              {{ scope.row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button type="primary" size="small">
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :disabled="!scope.row.enabled"
            >
              禁用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const users = ref([
  {
    id: 1,
    username: 'admin',
    realName: '系统管理员',
    idCard: '110101199001011234',
    phone: '13800138000',
    email: 'admin@hospital.com',
    roles: [{ id: 1, name: 'ADMIN' }],
    enabled: true,
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    username: 'doctor1',
    realName: '张医生',
    idCard: '110101198505055678',
    phone: '13800138001',
    email: 'doctor1@hospital.com',
    roles: [{ id: 2, name: 'DOCTOR' }],
    enabled: true,
    createTime: '2024-01-02 14:30:00'
  },
  {
    id: 3,
    username: 'patient1',
    realName: '李患者',
    idCard: '110101199510109876',
    phone: '13800138002',
    email: null,
    roles: [{ id: 3, name: 'PATIENT' }],
    enabled: true,
    createTime: '2024-01-03 09:15:00'
  }
])

const getRoleTagType = (roleName) => {
  const typeMap = {
    'ADMIN': 'danger',
    'DOCTOR': 'primary',
    'PATIENT': 'success',
    'REGISTRAR': 'warning'
  }
  return typeMap[roleName] || 'info'
}

const getRoleText = (roleName) => {
  const textMap = {
    'ADMIN': '管理员',
    'DOCTOR': '医生',
    'PATIENT': '患者',
    'REGISTRAR': '挂号员'
  }
  return textMap[roleName] || roleName
}

onMounted(() => {
})
</script>

<style scoped>
.user-management-page {
  padding: 0;
}

.list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
