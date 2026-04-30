<template>
  <div class="examinations-page">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>检查申请</span>
        </div>
      </template>
      
      <el-table
        :data="examinations"
        style="width: 100%"
        empty-text="暂无检查申请"
        v-loading="loading"
      >
        <el-table-column prop="examNo" label="检查单号" width="200" />
        <el-table-column prop="examType" label="检查类型" width="120" />
        <el-table-column prop="examName" label="检查项目" width="150" />
        <el-table-column prop="fee" label="费用" width="100">
          <template #default="scope">
            ¥{{ scope.row.fee }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showDetail(scope.row)">
              详情
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="success"
              size="small"
              @click="handlePay(scope.row)"
            >
              缴费
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="detailVisible" title="检查详情" width="600px">
      <el-descriptions :column="2" border v-if="currentExam">
        <el-descriptions-item label="检查单号" :span="1">
          {{ currentExam.examNo }}
        </el-descriptions-item>
        <el-descriptions-item label="检查类型" :span="1">
          {{ currentExam.examType }}
        </el-descriptions-item>
        <el-descriptions-item label="检查项目" :span="2">
          {{ currentExam.examName }}
        </el-descriptions-item>
        <el-descriptions-item label="检查描述" :span="2">
          {{ currentExam.examDescription || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="费用" :span="1">
          ¥{{ currentExam.fee }}
        </el-descriptions-item>
        <el-descriptions-item label="状态" :span="1">
          <el-tag :type="getStatusType(currentExam.status)">
            {{ getStatusText(currentExam.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="检查时间" :span="2">
          {{ currentExam.examTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="报告时间" :span="2">
          {{ currentExam.reportTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="检查结果" :span="2">
          {{ currentExam.examResult || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyExaminations } from '@/api/examination'

const loading = ref(false)
const examinations = ref([])
const detailVisible = ref(false)
const currentExam = ref(null)

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PAID': 'info',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'REPORTED': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待缴费',
    'PAID': '已缴费',
    'IN_PROGRESS': '检查中',
    'COMPLETED': '已完成',
    'REPORTED': '已出报告'
  }
  return textMap[status] || status
}

const loadExaminations = async () => {
  loading.value = true
  try {
    examinations.value = await getMyExaminations()
  } catch (error) {
    ElMessage.error('加载检查申请失败')
  } finally {
    loading.value = false
  }
}

const showDetail = (exam) => {
  currentExam.value = exam
  detailVisible.value = true
}

const handlePay = async (exam) => {
  try {
    await ElMessageBox.confirm(
      `确认支付检查费用 ¥${exam.fee}？`,
      '缴费确认',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    ElMessage.success('支付成功')
    loadExaminations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

onMounted(() => {
  loadExaminations()
})
</script>

<style scoped>
.examinations-page {
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
