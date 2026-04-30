<template>
  <div class="payments-page">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>费用缴纳</span>
        </div>
      </template>
      
      <el-table
        :data="payments"
        style="width: 100%"
        empty-text="暂无缴费记录"
        v-loading="loading"
      >
        <el-table-column prop="paymentNo" label="支付单号" width="200" />
        <el-table-column label="支付类型" width="100">
          <template #default="scope">
            {{ getPaymentTypeText(scope.row.paymentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="scope">
            <span style="font-weight: 600; color: #f56c6c">
              ¥{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="scope">
            {{ getPaymentMethodText(scope.row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="支付时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handlePay(scope.row)"
            >
              支付
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="info"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyPayments, processPayment, cancelPayment } from '@/api/payment'

const loading = ref(false)
const payments = ref([])

const getPaymentTypeText = (type) => {
  const typeMap = {
    'CONSULTATION': '诊疗费',
    'EXAMINATION': '检查费',
    'PRESCRIPTION': '处方费'
  }
  return typeMap[type] || type
}

const getPaymentMethodText = (method) => {
  const methodMap = {
    'WECHAT': '微信',
    'ALIPAY': '支付宝',
    'CASH': '现金',
    'CARD': '银行卡'
  }
  return methodMap[method] || '-'
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PAID': 'success',
    'REFUNDED': 'info',
    'CANCELLED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'REFUNDED': '已退款',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

const loadPayments = async () => {
  loading.value = true
  try {
    payments.value = await getMyPayments()
  } catch (error) {
    ElMessage.error('加载缴费记录失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async (payment) => {
  try {
    await ElMessageBox.confirm(
      `确认支付金额 ¥${payment.amount}？`,
      '支付确认',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    await processPayment(payment.id, 'WECHAT', 'TXN' + Date.now())
    ElMessage.success('支付成功')
    loadPayments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

const handleCancel = async (payment) => {
  try {
    await ElMessageBox.confirm(
      '确认取消该支付？',
      '取消支付',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cancelPayment(payment.id)
    ElMessage.success('已取消')
    loadPayments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadPayments()
})
</script>

<style scoped>
.payments-page {
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
