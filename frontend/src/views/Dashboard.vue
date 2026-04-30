<template>
  <div class="dashboard-page">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card stat-blue">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalAppointments }}</div>
            <div class="stat-label">今日总预约数</div>
          </div>
          <div class="stat-icon">
            <el-icon><Calendar /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card stat-green">
          <div class="stat-content">
            <div class="stat-number">{{ stats.completedCount }}</div>
            <div class="stat-label">已完成就诊</div>
          </div>
          <div class="stat-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card stat-orange">
          <div class="stat-content">
            <div class="stat-number">{{ stats.checkedInCount }}</div>
            <div class="stat-label">已签到待诊</div>
          </div>
          <div class="stat-icon">
            <el-icon><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card stat-red">
          <div class="stat-content">
            <div class="stat-number">{{ stats.noShowRate }}%</div>
            <div class="stat-label">爽约率</div>
          </div>
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>科室负荷统计</span>
              <el-date-picker
                v-model="selectedDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                size="small"
                style="margin-left: 16px"
                @change="loadDepartmentLoad"
              />
            </div>
          </template>
          
          <div class="chart-container" v-loading="loading">
            <div class="department-bars">
              <div
                v-for="(load, dept) in departmentLoad"
                :key="dept"
                class="bar-item"
              >
                <div class="bar-label">{{ dept }}</div>
                <div class="bar-wrapper">
                  <div
                    class="bar-fill"
                    :style="{ width: getBarWidth(load) + '%' }"
                  >
                    <span>{{ load }} 人</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>今日排班医生</span>
            </div>
          </template>
          
          <div class="doctor-list" v-loading="loading">
            <div
              v-for="schedule in todaySchedules"
              :key="schedule.id"
              class="doctor-item"
            >
              <el-avatar :size="40" class="doctor-avatar">
                {{ schedule.doctor?.user?.realName?.charAt(0) }}
              </el-avatar>
              <div class="doctor-info">
                <div class="doctor-name">
                  {{ schedule.doctor?.user?.realName }}
                  <el-tag size="small" type="info">{{ schedule.doctor?.title }}</el-tag>
                </div>
                <div class="doctor-detail">
                  {{ schedule.department?.name }} · {{ schedule.startTime }}-{{ schedule.endTime }}
                </div>
              </div>
            </div>
            
            <el-empty v-if="todaySchedules.length === 0" description="暂无排班" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="revenue-card">
          <template #header>
            <div class="card-header">
              <span>收入统计</span>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                size="small"
                style="margin-left: 16px"
                @change="loadRevenue"
              />
            </div>
          </template>
          
          <div class="revenue-stats">
            <div class="revenue-item">
              <div class="revenue-label">总收入</div>
              <div class="revenue-value">¥ {{ revenueStats.totalRevenue || '0.00' }}</div>
            </div>
            <div class="revenue-item">
              <div class="revenue-label">支付笔数</div>
              <div class="revenue-value">{{ revenueStats.paymentCount || 0 }} 笔</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <router-link to="/schedule" class="action-link">
                <el-card shadow="hover" class="action-card">
                  <el-icon class="action-icon"><Clock /></el-icon>
                  <div class="action-text">排班管理</div>
                </el-card>
              </router-link>
            </el-col>
            <el-col :span="8">
              <router-link to="/user-management" class="action-link">
                <el-card shadow="hover" class="action-card">
                  <el-icon class="action-icon"><UserFilled /></el-icon>
                  <div class="action-text">用户管理</div>
                </el-card>
              </router-link>
            </el-col>
            <el-col :span="8">
              <router-link to="/appointment" class="action-link">
                <el-card shadow="hover" class="action-card">
                  <el-icon class="action-icon"><Calendar /></el-icon>
                  <div class="action-text">预约挂号</div>
                </el-card>
              </router-link>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getTodayStatistics, getDepartmentLoad, getRevenueStatistics } from '@/api/dashboard'
import { getSchedulesByDate } from '@/api/schedule'
import dayjs from 'dayjs'

const loading = ref(false)
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const dateRange = ref([
  dayjs().startOf('month').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])

const stats = reactive({
  totalAppointments: 0,
  completedCount: 0,
  checkedInCount: 0,
  noShowCount: 0,
  noShowRate: '0.00'
})

const departmentLoad = ref({})
const todaySchedules = ref([])
const revenueStats = reactive({
  totalRevenue: '0.00',
  paymentCount: 0
})

const getBarWidth = (load) => {
  const maxLoad = Math.max(...Object.values(departmentLoad.value), 1)
  return (load / maxLoad) * 100
}

const loadTodayStats = async () => {
  loading.value = true
  try {
    const result = await getTodayStatistics()
    Object.assign(stats, result)
  } catch (error) {
    console.error('加载今日统计失败:', error)
  } finally {
    loading.value = false
  }
}

const loadDepartmentLoad = async () => {
  try {
    const result = await getDepartmentLoad(selectedDate.value)
    departmentLoad.value = result.loadData || {}
  } catch (error) {
    console.error('加载科室负荷失败:', error)
  }
}

const loadTodaySchedules = async () => {
  try {
    todaySchedules.value = await getSchedulesByDate(dayjs().format('YYYY-MM-DD'))
  } catch (error) {
    console.error('加载今日排班失败:', error)
  }
}

const loadRevenue = async () => {
  if (!dateRange.value || dateRange.value.length < 2) return
  
  try {
    const result = await getRevenueStatistics(dateRange.value[0], dateRange.value[1])
    Object.assign(revenueStats, result)
  } catch (error) {
    console.error('加载收入统计失败:', error)
  }
}

onMounted(() => {
  loadTodayStats()
  loadDepartmentLoad()
  loadTodaySchedules()
  loadRevenue()
})
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-radius: 8px;
}

.stat-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.stat-orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-red {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}

.chart-card,
.info-card,
.revenue-card,
.quick-actions-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  min-height: 300px;
}

.department-bars {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 10px 0;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 100px;
  text-align: right;
  font-weight: 500;
  color: #606266;
}

.bar-wrapper {
  flex: 1;
  background-color: #ebeef5;
  border-radius: 4px;
  height: 24px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF 0%, #67c23a 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 8px;
  transition: width 0.3s ease;
  min-width: 40px;
}

.bar-fill span {
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.doctor-list {
  max-height: 400px;
  overflow-y: auto;
}

.doctor-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
}

.doctor-item:last-child {
  border-bottom: none;
}

.doctor-avatar {
  background-color: #409EFF;
}

.doctor-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.doctor-detail {
  font-size: 12px;
  color: #909399;
}

.revenue-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.revenue-item {
  text-align: center;
}

.revenue-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.revenue-value {
  font-size: 28px;
  font-weight: 700;
  color: #409EFF;
}

.action-link {
  text-decoration: none;
}

.action-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-2px);
}

.action-icon {
  font-size: 36px;
  color: #409EFF;
  margin-bottom: 8px;
}

.action-text {
  font-size: 14px;
  color: #606266;
}
</style>
