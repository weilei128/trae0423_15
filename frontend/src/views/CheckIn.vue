<template>
  <div class="check-in-page">
    <el-card class="scan-card">
      <template #header>
        <div class="card-header">
          <span>就诊签到</span>
        </div>
      </template>
      
      <div class="scan-container">
        <div class="scan-icon">
          <el-icon><QrCode /></el-icon>
        </div>
        
        <el-form :model="checkInForm" :inline="true">
          <el-form-item label="预约单号">
            <el-input
              v-model="checkInForm.appointmentNo"
              placeholder="请输入预约单号或扫描二维码"
              style="width: 400px"
              size="large"
            >
              <template #append>
                <el-button type="primary" :loading="loading" @click="handleCheckIn">
                  <el-icon><CircleCheck /></el-icon>
                  签到
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
        
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          style="margin-top: 20px; max-width: 600px"
        >
          <template #default>
            请输入预约单号进行签到，或扫描就诊卡二维码完成签到。
            <br />签到成功后，请前往对应科室候诊区等候叫号。
          </template>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { checkIn } from '@/api/checkin'

const loading = ref(false)

const checkInForm = reactive({
  appointmentNo: ''
})

const handleCheckIn = async () => {
  if (!checkInForm.appointmentNo.trim()) {
    ElMessage.warning('请输入预约单号')
    return
  }
  
  loading.value = true
  try {
    const result = await checkIn(checkInForm.appointmentNo.trim())
    ElMessage.success('签到成功！请前往对应科室候诊')
    checkInForm.appointmentNo = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '签到失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.check-in-page {
  padding: 0;
}

.scan-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.scan-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.scan-icon {
  font-size: 80px;
  color: #409EFF;
  margin-bottom: 30px;
}
</style>
