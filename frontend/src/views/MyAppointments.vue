<template>
  <div class="my-appointments-page">
    <el-card class="filter-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="全部状态"
            clearable
            style="width: 150px"
          >
            <el-option label="待就诊" value="PENDING" />
            <el-option label="已签到" value="CHECKED_IN" />
            <el-option label="就诊中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已爽约" value="NO_SHOW" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadAppointments">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="appointments-card" v-loading="loading">
      <el-table :data="appointments" style="width: 100%" empty-text="暂无预约记录">
        <el-table-column prop="appointmentNo" label="预约单号" width="200" />
        <el-table-column prop="department.name" label="科室" width="120" />
        <el-table-column prop="doctor.user.realName" label="医生" width="100" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column label="预约时间" width="120">
          <template #default="scope">
            {{ scope.row.appointmentTime }}
          </template>
        </el-table-column>
        <el-table-column prop="queueNumber" label="序号" width="80">
          <template #default="scope">
            <el-tag type="primary">{{ scope.row.queueNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="showAppointmentDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消预约
            </el-button>
            <el-button
              v-else
              type="primary"
              size="small"
              @click="showAppointmentDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="detailVisible"
      title="预约详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentAppointment">
        <el-descriptions-item label="预约单号">{{ currentAppointment.appointmentNo }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentAppointment.department?.name }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ currentAppointment.doctor?.user?.realName }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentAppointment.doctor?.title }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentAppointment.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentAppointment.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="就诊序号">{{ currentAppointment.queueNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签到时间" :span="2">
          {{ currentAppointment.checkInTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ currentAppointment.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAppointments, cancelAppointment } from '@/api/appointment'

const loading = ref(false)
const appointments = ref([])
const detailVisible = ref(false)
const currentAppointment = ref(null)

const filterForm = reactive({
  status: null
})

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CHECKED_IN': 'success',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'info',
    'NO_SHOW': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待就诊',
    'CHECKED_IN': '已签到',
    'IN_PROGRESS': '就诊中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'NO_SHOW': '已爽约'
  }
  return textMap[status] || status
}

const loadAppointments = async () => {
  loading.value = true
  try {
    let result = await getMyAppointments()
    if (filterForm.status) {
      result = result.filter(item => item.status === filterForm.status)
    }
    appointments.value = result
  } catch (error) {
    ElMessage.error('加载预约记录失败')
  } finally {
    loading.value = false
  }
}

const showAppointmentDetail = (appointment) => {
  currentAppointment.value = appointment
  detailVisible.value = true
}

const handleCancel = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该预约吗？',
      '取消预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cancelAppointment(appointment.id)
    ElMessage.success('取消预约成功')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '取消预约失败')
    }
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.my-appointments-page {
  padding: 0;
}

.filter-card,
.appointments-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
