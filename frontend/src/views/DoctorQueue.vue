<template>
  <div class="doctor-queue-page">
    <el-card class="header-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="queue-stat">
            <div class="stat-number">{{ waitingCount }}</div>
            <div class="stat-label">待诊人数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="queue-stat">
            <div class="stat-number">{{ inProgressCount }}</div>
            <div class="stat-label">就诊中</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="queue-stat">
            <div class="stat-number">{{ completedCount }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="queue-stat">
            <div class="stat-number">{{ noShowCount }}</div>
            <div class="stat-label">爽约</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-card class="queue-card">
      <template #header>
        <div class="card-header">
          <span>今日候诊队列</span>
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            size="small"
            style="margin-left: 16px"
            @change="loadAppointments"
          />
        </div>
      </template>
      
      <el-table
        :data="appointments"
        style="width: 100%"
        empty-text="暂无候诊患者"
        v-loading="loading"
      >
        <el-table-column prop="queueNumber" label="序号" width="80">
          <template #default="scope">
            <el-tag :type="getQueueTagType(scope.row.status)" size="large">
              {{ scope.row.queueNumber }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="patient.realName" label="患者姓名" width="120" />
        <el-table-column prop="patient.idCard" label="身份证号" width="200">
          <template #default="scope">
            {{ scope.row.patient?.idCard }}
          </template>
        </el-table-column>
        <el-table-column prop="patient.phone" label="联系电话" width="140">
          <template #default="scope">
            {{ scope.row.patient?.phone }}
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="120">
          <template #default="scope">
            {{ scope.row.appointmentTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" width="180">
          <template #default="scope">
            {{ scope.row.checkInTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'CHECKED_IN'"
              type="primary"
              size="small"
              @click="handleStartConsultation(scope.row)"
            >
              开始就诊
            </el-button>
            <el-button
              v-if="scope.row.status === 'IN_PROGRESS'"
              type="success"
              size="small"
              @click="handleCompleteConsultation(scope.row)"
            >
              完成就诊
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="warning"
              size="small"
              @click="handleMarkNoShow(scope.row)"
            >
              标记爽约
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="showMedicalRecord(scope.row)"
            >
              病历
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="medicalRecordVisible"
      title="病历信息"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentMedicalRecord">
        <el-descriptions-item label="科室" :span="1">
          {{ currentAppointment?.department?.name }}
        </el-descriptions-item>
        <el-descriptions-item label="医生" :span="1">
          {{ currentAppointment?.doctor?.user?.realName }}
        </el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">
          {{ currentMedicalRecord.chiefComplaint || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="现病史" :span="2">
          {{ currentMedicalRecord.presentIllness || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="既往史" :span="2">
          {{ currentMedicalRecord.pastHistory || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体格检查" :span="2">
          {{ currentMedicalRecord.physicalExamination || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="诊断" :span="2">
          {{ currentMedicalRecord.diagnosis || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="治疗方案" :span="2">
          {{ currentMedicalRecord.treatmentPlan || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处方" :span="2">
          {{ currentMedicalRecord.prescription || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div style="margin-top: 20px" v-if="!currentMedicalRecord">
        <el-empty description="暂无病历记录" />
      </div>
      
      <template #footer>
        <el-button @click="medicalRecordVisible = false">关闭</el-button>
        <el-button v-if="!currentMedicalRecord && currentAppointment?.status === 'IN_PROGRESS'" type="primary" @click="showCreateRecordDialog">
          新建病历
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="createRecordVisible"
      title="新建病历"
      width="700px"
    >
      <el-form
        ref="medicalRecordFormRef"
        :model="medicalRecordForm"
        :rules="medicalRecordRules"
        label-width="100px"
      >
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input
            v-model="medicalRecordForm.chiefComplaint"
            type="textarea"
            :rows="2"
            placeholder="请输入主诉"
          />
        </el-form-item>
        
        <el-form-item label="现病史" prop="presentIllness">
          <el-input
            v-model="medicalRecordForm.presentIllness"
            type="textarea"
            :rows="3"
            placeholder="请输入现病史"
          />
        </el-form-item>
        
        <el-form-item label="既往史">
          <el-input
            v-model="medicalRecordForm.pastHistory"
            type="textarea"
            :rows="2"
            placeholder="请输入既往史"
          />
        </el-form-item>
        
        <el-form-item label="体格检查">
          <el-input
            v-model="medicalRecordForm.physicalExamination"
            type="textarea"
            :rows="2"
            placeholder="请输入体格检查"
          />
        </el-form-item>
        
        <el-form-item label="诊断" prop="diagnosis">
          <el-input
            v-model="medicalRecordForm.diagnosis"
            type="textarea"
            :rows="2"
            placeholder="请输入诊断"
          />
        </el-form-item>
        
        <el-form-item label="治疗方案">
          <el-input
            v-model="medicalRecordForm.treatmentPlan"
            type="textarea"
            :rows="2"
            placeholder="请输入治疗方案"
          />
        </el-form-item>
        
        <el-form-item label="处方">
          <el-input
            v-model="medicalRecordForm.prescription"
            type="textarea"
            :rows="2"
            placeholder="请输入处方"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createRecordVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitMedicalRecord">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDoctorAppointmentsByDate } from '@/api/appointment'
import { completeConsultation, startConsultation, markNoShow } from '@/api/checkin'
import { getMedicalRecordByAppointment, createMedicalRecord } from '@/api/medicalRecord'
import dayjs from 'dayjs'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const appointments = ref([])

const medicalRecordVisible = ref(false)
const createRecordVisible = ref(false)
const currentAppointment = ref(null)
const currentMedicalRecord = ref(null)
const medicalRecordFormRef = ref(null)

const medicalRecordForm = reactive({
  appointment: null,
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExamination: '',
  diagnosis: '',
  treatmentPlan: '',
  prescription: ''
})

const medicalRecordRules = {
  chiefComplaint: [{ required: true, message: '请输入主诉', trigger: 'blur' }],
  presentIllness: [{ required: true, message: '请输入现病史', trigger: 'blur' }],
  diagnosis: [{ required: true, message: '请输入诊断', trigger: 'blur' }]
}

const waitingCount = computed(() => {
  return appointments.value.filter(a => a.status === 'CHECKED_IN').length
})

const inProgressCount = computed(() => {
  return appointments.value.filter(a => a.status === 'IN_PROGRESS').length
})

const completedCount = computed(() => {
  return appointments.value.filter(a => a.status === 'COMPLETED').length
})

const noShowCount = computed(() => {
  return appointments.value.filter(a => a.status === 'NO_SHOW').length
})

const getQueueTagType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'CHECKED_IN': 'success',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'primary',
    'NO_SHOW': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'CHECKED_IN': 'success',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'primary',
    'NO_SHOW': 'danger',
    'CANCELLED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待签到',
    'CHECKED_IN': '已签到',
    'IN_PROGRESS': '就诊中',
    'COMPLETED': '已完成',
    'NO_SHOW': '已爽约',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const doctorId = userStore.user?.id
    if (doctorId) {
      appointments.value = await getDoctorAppointmentsByDate(doctorId, selectedDate.value)
    }
  } catch (error) {
    ElMessage.error('加载候诊队列失败')
  } finally {
    loading.value = false
  }
}

const handleStartConsultation = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确认开始为 ${appointment.patient?.realName} 就诊？`,
      '开始就诊',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await startConsultation(appointment.id)
    ElMessage.success('已开始就诊')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleCompleteConsultation = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确认完成 ${appointment.patient?.realName} 的就诊？`,
      '完成就诊',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    await completeConsultation(appointment.id)
    ElMessage.success('就诊已完成')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleMarkNoShow = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确认将 ${appointment.patient?.realName} 标记为爽约？`,
      '标记爽约',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await markNoShow(appointment.id)
    ElMessage.success('已标记为爽约')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const showMedicalRecord = async (appointment) => {
  currentAppointment.value = appointment
  try {
    currentMedicalRecord.value = await getMedicalRecordByAppointment(appointment.id)
  } catch (error) {
    currentMedicalRecord.value = null
  }
  medicalRecordVisible.value = true
}

const showCreateRecordDialog = () => {
  medicalRecordForm.appointment = { id: currentAppointment.value.id }
  medicalRecordForm.chiefComplaint = ''
  medicalRecordForm.presentIllness = ''
  medicalRecordForm.pastHistory = ''
  medicalRecordForm.physicalExamination = ''
  medicalRecordForm.diagnosis = ''
  medicalRecordForm.treatmentPlan = ''
  medicalRecordForm.prescription = ''
  medicalRecordVisible.value = false
  createRecordVisible.value = true
}

const handleSubmitMedicalRecord = async () => {
  const valid = await medicalRecordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await createMedicalRecord({ ...medicalRecordForm })
    ElMessage.success('病历保存成功')
    createRecordVisible.value = false
    await showMedicalRecord(currentAppointment.value)
    medicalRecordVisible.value = true
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.doctor-queue-page {
  padding: 0;
}

.header-card {
  margin-bottom: 20px;
}

.queue-stat {
  text-align: center;
  padding: 10px;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.queue-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
