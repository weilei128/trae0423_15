<template>
  <div class="appointment-page">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>预约挂号</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="选择科室">
          <el-select
            v-model="searchForm.departmentId"
            placeholder="请选择科室"
            clearable
            style="width: 200px"
            @change="handleDepartmentChange"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择医生">
          <el-select
            v-model="searchForm.doctorId"
            placeholder="请选择医生"
            clearable
            style="width: 200px"
            :disabled="!searchForm.departmentId"
          >
            <el-option
              v-for="doctor in doctors"
              :key="doctor.id"
              :label="doctor.realName"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="searchSchedules">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="schedules-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>可预约号源</span>
        </div>
      </template>
      
      <el-table :data="schedules" style="width: 100%" empty-text="暂无可预约号源">
        <el-table-column prop="department.name" label="科室" width="150" />
        <el-table-column prop="doctor.user.realName" label="医生" width="120" />
        <el-table-column prop="doctor.title" label="职称" width="100" />
        <el-table-column prop="scheduleDate" label="日期" width="120">
          <template #default="scope">
            {{ scope.row.scheduleDate }}
          </template>
        </el-table-column>
        <el-table-column prop="timeSlot" label="时段" width="120">
          <template #default="scope">
            {{ scope.row.startTime }} - {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="maxPatients" label="号源总数" width="100" />
        <el-table-column prop="remainingPatients" label="剩余号源" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.remainingPatients > 0 ? 'success' : 'danger'">
              {{ scope.row.remainingPatients }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getScheduleStatusType(scope.row.status)">
              {{ getScheduleStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              :disabled="scope.row.status !== 'AVAILABLE' || scope.row.remainingPatients <= 0"
              @click="handleAppointment(scope.row)"
            >
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartments, getDoctorsByDepartment, getSchedulesByDate, getSchedulesByDepartmentAndDate, getSchedulesByDoctorAndDate } from '@/api/schedule'
import { createAppointment } from '@/api/appointment'
import dayjs from 'dayjs'

const loading = ref(false)
const departments = ref([])
const doctors = ref([])
const schedules = ref([])

const searchForm = reactive({
  departmentId: null,
  doctorId: null,
  date: dayjs().format('YYYY-MM-DD')
})

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getScheduleStatusType = (status) => {
  const statusMap = {
    'AVAILABLE': 'success',
    'FULL': 'warning',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getScheduleStatusText = (status) => {
  const statusMap = {
    'AVAILABLE': '可预约',
    'FULL': '已满',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const handleDepartmentChange = (departmentId) => {
  searchForm.doctorId = null
  if (departmentId) {
    loadDoctors(departmentId)
  } else {
    doctors.value = []
  }
}

const loadDepartments = async () => {
  try {
    departments.value = await getDepartments()
  } catch (error) {
    ElMessage.error('加载科室列表失败')
  }
}

const loadDoctors = async (departmentId) => {
  try {
    doctors.value = await getDoctorsByDepartment(departmentId)
  } catch (error) {
    ElMessage.error('加载医生列表失败')
  }
}

const searchSchedules = async () => {
  loading.value = true
  try {
    let result
    if (searchForm.doctorId) {
      result = await getSchedulesByDoctorAndDate(searchForm.doctorId, searchForm.date)
    } else if (searchForm.departmentId) {
      result = await getSchedulesByDepartmentAndDate(searchForm.departmentId, searchForm.date)
    } else {
      result = await getSchedulesByDate(searchForm.date)
    }
    schedules.value = result
  } catch (error) {
    ElMessage.error('查询排班失败')
  } finally {
    loading.value = false
  }
}

const handleAppointment = async (schedule) => {
  try {
    await ElMessageBox.confirm(
      `确认预约${schedule.doctor.user.realName}医生的号源吗？`,
      '预约确认',
      {
        confirmButtonText: '确认预约',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await createAppointment(schedule.id)
    ElMessage.success('预约成功')
    searchSchedules()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '预约失败')
    }
  }
}

onMounted(() => {
  loadDepartments()
  searchSchedules()
})
</script>

<style scoped>
.appointment-page {
  padding: 0;
}

.search-card,
.schedules-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  flex-wrap: wrap;
}
</style>
