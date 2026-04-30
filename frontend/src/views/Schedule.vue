<template>
  <div class="schedule-page">
    <el-card class="action-card">
      <template #header>
        <div class="card-header">
          <span>排班管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新增排班
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="科室">
          <el-select
            v-model="searchForm.departmentId"
            placeholder="全部科室"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 180px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadSchedules">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="schedules-card" v-loading="loading">
      <el-table :data="schedules" style="width: 100%" empty-text="暂无排班记录">
        <el-table-column prop="department.name" label="科室" width="150" />
        <el-table-column label="医生" width="120">
          <template #default="scope">
            {{ scope.row.doctor?.user?.realName }}
          </template>
        </el-table-column>
        <el-table-column prop="doctor.title" label="职称" width="100" />
        <el-table-column prop="scheduleDate" label="日期" width="120" />
        <el-table-column label="时段" width="180">
          <template #default="scope">
            {{ scope.row.startTime }} - {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="号源情况" width="180">
          <template #default="scope">
            <el-progress
              :percentage="((scope.row.maxPatients - scope.row.remainingPatients) / scope.row.maxPatients) * 100"
              :format="() => `${scope.row.maxPatients - scope.row.remainingPatients}/${scope.row.maxPatients}`"
            />
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
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :disabled="scope.row.status === 'CANCELLED'"
              @click="handleDelete(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑排班' : '新增排班'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="scheduleFormRef"
        :model="scheduleForm"
        :rules="scheduleRules"
        label-width="100px"
      >
        <el-form-item label="科室" prop="departmentId">
          <el-select
            v-model="scheduleForm.departmentId"
            placeholder="请选择科室"
            style="width: 100%"
            @change="handleDepartmentSelect"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="医生" prop="doctorId">
          <el-select
            v-model="scheduleForm.doctorId"
            placeholder="请选择医生"
            style="width: 100%"
            :disabled="!scheduleForm.departmentId"
          >
            <el-option
              v-for="doctor in departmentDoctors"
              :key="doctor.id"
              :label="doctor.user?.realName + ' (' + doctor.title + ')'"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker
            v-model="scheduleForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-select
                v-model="scheduleForm.startTime"
                placeholder="选择开始时间"
                start="08:00"
                step="00:30"
                end="18:00"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-select
                v-model="scheduleForm.endTime"
                placeholder="选择结束时间"
                start="08:00"
                step="00:30"
                end="18:00"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="最大号源数" prop="maxPatients">
          <el-input-number
            v-model="scheduleForm.maxPatients"
            :min="1"
            :max="100"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartments, getDoctorsByDepartment, getSchedulesByDate, getSchedulesByDepartmentAndDate, createSchedule, updateSchedule, deleteSchedule } from '@/api/schedule'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const scheduleFormRef = ref(null)

const departments = ref([])
const departmentDoctors = ref([])
const schedules = ref([])

const searchForm = reactive({
  departmentId: null,
  date: null
})

const scheduleForm = reactive({
  id: null,
  departmentId: null,
  doctorId: null,
  scheduleDate: null,
  startTime: null,
  endTime: null,
  maxPatients: 20,
  timeSlot: null
})

const scheduleRules = {
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  maxPatients: [{ required: true, message: '请输入最大号源数', trigger: 'blur' }]
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getStatusType = (status) => {
  const typeMap = {
    'AVAILABLE': 'success',
    'FULL': 'warning',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'AVAILABLE': '可预约',
    'FULL': '已满',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

const loadDepartments = async () => {
  try {
    departments.value = await getDepartments()
  } catch (error) {
    ElMessage.error('加载科室列表失败')
  }
}

const handleDepartmentSelect = async (departmentId) => {
  scheduleForm.doctorId = null
  if (departmentId) {
    try {
      departmentDoctors.value = await getDoctorsByDepartment(departmentId)
    } catch (error) {
      ElMessage.error('加载医生列表失败')
    }
  } else {
    departmentDoctors.value = []
  }
}

const loadSchedules = async () => {
  loading.value = true
  try {
    let result
    if (searchForm.departmentId) {
      result = await getSchedulesByDepartmentAndDate(searchForm.departmentId, searchForm.date || dayjs().format('YYYY-MM-DD'))
    } else if (searchForm.date) {
      result = await getSchedulesByDate(searchForm.date)
    } else {
      result = await getSchedulesByDate(dayjs().format('YYYY-MM-DD'))
    }
    schedules.value = result
  } catch (error) {
    ElMessage.error('加载排班列表失败')
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  scheduleForm.id = row.id
  scheduleForm.departmentId = row.department.id
  scheduleForm.doctorId = row.doctor.id
  scheduleForm.scheduleDate = row.scheduleDate
  scheduleForm.startTime = row.startTime
  scheduleForm.endTime = row.endTime
  scheduleForm.maxPatients = row.maxPatients
  handleDepartmentSelect(row.department.id)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该排班吗？',
      '取消排班',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteSchedule(row.id)
    ElMessage.success('取消排班成功')
    loadSchedules()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '取消排班失败')
    }
  }
}

const handleSubmit = async () => {
  const valid = await scheduleFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    const scheduleData = {
      doctor: { id: scheduleForm.doctorId },
      department: { id: scheduleForm.departmentId },
      scheduleDate: scheduleForm.scheduleDate,
      startTime: scheduleForm.startTime,
      endTime: scheduleForm.endTime,
      maxPatients: scheduleForm.maxPatients,
      timeSlot: `${scheduleForm.startTime}-${scheduleForm.endTime}`,
      status: 'AVAILABLE'
    }
    
    if (isEdit.value) {
      scheduleData.id = scheduleForm.id
      await updateSchedule(scheduleForm.id, scheduleData)
      ElMessage.success('更新排班成功')
    } else {
      await createSchedule(scheduleData)
      ElMessage.success('新增排班成功')
    }
    
    dialogVisible.value = false
    searchForm.date = scheduleForm.scheduleDate
    searchForm.departmentId = scheduleForm.departmentId
    loadSchedules()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  scheduleForm.id = null
  scheduleForm.departmentId = null
  scheduleForm.doctorId = null
  scheduleForm.scheduleDate = null
  scheduleForm.startTime = null
  scheduleForm.endTime = null
  scheduleForm.maxPatients = 20
  scheduleForm.timeSlot = null
  departmentDoctors.value = []
  scheduleFormRef.value?.resetFields()
}

onMounted(() => {
  loadDepartments()
  loadSchedules()
})
</script>

<style scoped>
.schedule-page {
  padding: 0;
}

.action-card,
.schedules-card {
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

.search-form {
  flex-wrap: wrap;
}

.dialog-footer {
  text-align: right;
}
</style>
