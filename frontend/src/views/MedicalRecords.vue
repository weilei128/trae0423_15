<template>
  <div class="medical-records-page">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>病历记录</span>
        </div>
      </template>
      
      <el-table
        :data="medicalRecords"
        style="width: 100%"
        empty-text="暂无病历记录"
        v-loading="loading"
      >
        <el-table-column prop="appointment.appointmentNo" label="预约单号" width="200" />
        <el-table-column label="科室" width="120">
          <template #default="scope">
            {{ scope.row.appointment?.department?.name }}
          </template>
        </el-table-column>
        <el-table-column label="医生" width="100">
          <template #default="scope">
            {{ scope.row.appointment?.doctor?.user?.realName }}
          </template>
        </el-table-column>
        <el-table-column prop="chiefComplaint" label="主诉" min-width="150" />
        <el-table-column prop="diagnosis" label="诊断" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="100">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="detailVisible" title="病历详情" width="700px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="预约单号" :span="1">
          {{ currentRecord.appointment?.appointmentNo }}
        </el-descriptions-item>
        <el-descriptions-item label="科室" :span="1">
          {{ currentRecord.appointment?.department?.name }}
        </el-descriptions-item>
        <el-descriptions-item label="医生" :span="1">
          {{ currentRecord.appointment?.doctor?.user?.realName }}
        </el-descriptions-item>
        <el-descriptions-item label="就诊日期" :span="1">
          {{ currentRecord.appointment?.appointmentDate }}
        </el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">
          {{ currentRecord.chiefComplaint || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="现病史" :span="2">
          {{ currentRecord.presentIllness || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="既往史" :span="2">
          {{ currentRecord.pastHistory || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="体格检查" :span="2">
          {{ currentRecord.physicalExamination || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="诊断" :span="2">
          {{ currentRecord.diagnosis || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="治疗方案" :span="2">
          {{ currentRecord.treatmentPlan || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处方" :span="2">
          {{ currentRecord.prescription || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ currentRecord.createTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyMedicalRecords } from '@/api/medicalRecord'

const loading = ref(false)
const medicalRecords = ref([])
const detailVisible = ref(false)
const currentRecord = ref(null)

const loadMedicalRecords = async () => {
  loading.value = true
  try {
    medicalRecords.value = await getMyMedicalRecords()
  } catch (error) {
    ElMessage.error('加载病历记录失败')
  } finally {
    loading.value = false
  }
}

const showDetail = (record) => {
  currentRecord.value = record
  detailVisible.value = true
}

onMounted(() => {
  loadMedicalRecords()
})
</script>

<style scoped>
.medical-records-page {
  padding: 0;
}

.list-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
