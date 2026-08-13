<!-- 数据统计：ECharts 图表（性别/职位/学历饼图、班级人数柱状图）+ 操作日志分页表格 -->
<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  empGenderData, empJobData, studentDegreeData, studentCountData, pageLog
} from '../api/report'
import { formatDateTime } from '../utils/format'
import Pagination from '../components/Pagination.vue'

/* ---------------- 图表 ---------------- */

// 四个图表容器的 DOM 引用
const genderChartEl = ref(null)
const jobChartEl = ref(null)
const degreeChartEl = ref(null)
const countChartEl = ref(null)

// ECharts 实例缓存（卸载时统一销毁）
let charts = []

// 通用饼图渲染
function renderPie(el, title, data) {
  const chart = echarts.init(el)
  chart.setOption({
    title: { text: title, left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['35%', '60%'], // 环形饼图
      center: ['50%', '52%'],
      data, // [{name, value}]
      label: { formatter: '{b}\n{c} 人' }
    }]
  })
  charts.push(chart)
}

// 班级人数柱状图渲染（双数组模式：名称数组 + 人数数组）
function renderBar(el, title, names, counts) {
  const chart = echarts.init(el)
  chart.setOption({
    title: { text: title, left: 'center', textStyle: { fontSize: 15 } },
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, bottom: 60, top: 50 },
    xAxis: {
      type: 'category',
      data: names,
      // 班级名较长，倾斜 30 度显示
      axisLabel: { rotate: 30, interval: 0, fontSize: 11 }
    },
    yAxis: { type: 'value', name: '人数', minInterval: 1 },
    series: [{
      type: 'bar',
      data: counts,
      barMaxWidth: 36,
      itemStyle: { color: '#3370ff', borderRadius: [4, 4, 0, 0] }
    }]
  })
  charts.push(chart)
}

// 加载全部统计数据并渲染图表
async function loadCharts() {
  // 等 DOM 挂载完成再初始化图表
  await nextTick()

  // 1. 员工性别统计（对象数组：name/value 可直接用）
  const gender = await empGenderData()
  renderPie(genderChartEl.value, '员工性别分布', gender)

  // 2. 员工职位统计（字段为 job/value，需映射成 name/value）
  const job = await empJobData()
  renderPie(jobChartEl.value, '员工职位分布', job.map((i) => ({ name: i.job, value: i.value })))

  // 3. 学员学历统计（字段为 degree/value，同样映射）
  const degree = await studentDegreeData()
  renderPie(degreeChartEl.value, '学员学历分布', degree.map((i) => ({ name: i.degree, value: i.value })))

  // 4. 班级人数统计（双数组模式）
  const count = await studentCountData()
  renderBar(countChartEl.value, '各班级学员人数', count.clazzNameList || [], count.studentCountList || [])
}

// 窗口尺寸变化时重绘图表
function handleResize() {
  charts.forEach((c) => c.resize())
}

/* ---------------- 操作日志 ---------------- */

const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const logList = ref([])
const logLoading = ref(false)

// 加载操作日志分页
async function loadLog() {
  logLoading.value = true
  try {
    const data = await pageLog({ pageNo: pageNo.value, pageSize: pageSize.value })
    logList.value = data.list || []
    total.value = data.total || 0
  } finally {
    logLoading.value = false
  }
}

function handlePageChange(p) {
  pageNo.value = p
  loadLog()
}

/* ---------------- 生命周期 ---------------- */

onMounted(() => {
  loadCharts()
  loadLog()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例，避免内存泄漏
  charts.forEach((c) => c.dispose())
  charts = []
})
</script>

<template>
  <div>
    <!-- 图表区域：2 x 2 网格 -->
    <div class="chart-grid">
      <div class="card"><div ref="genderChartEl" class="chart-box"></div></div>
      <div class="card"><div ref="jobChartEl" class="chart-box"></div></div>
      <div class="card"><div ref="degreeChartEl" class="chart-box"></div></div>
      <div class="card"><div ref="countChartEl" class="chart-box"></div></div>
    </div>

    <!-- 操作日志表格 -->
    <div class="card log-card">
      <div class="toolbar">
        <h3>操作日志</h3>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th style="width: 60px">ID</th>
            <th>操作人</th>
            <th>操作时间</th>
            <th>类名.方法名</th>
            <th style="width: 90px">耗时(ms)</th>
            <th>返回值</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="logLoading">
            <td colspan="6" class="empty">加载中...</td>
          </tr>
          <tr v-else-if="logList.length === 0">
            <td colspan="6" class="empty">暂无日志</td>
          </tr>
          <tr v-for="log in logList" :key="log.id">
            <td>{{ log.id }}</td>
            <!-- AOP 切面部分字段尚未填充，统一用 '-' 兜底 -->
            <td>{{ log.operateEmpName || (log.operateEmpId ? `员工#${log.operateEmpId}` : '-') }}</td>
            <td>{{ formatDateTime(log.operateTime) || '-' }}</td>
            <td class="mono">{{ log.className ? `${log.className}.${log.methodName}` : '-' }}</td>
            <td>{{ log.costTime }}</td>
            <!-- 返回值可能很长，截断显示并支持悬停查看 -->
            <td class="return-cell" :title="log.returnValue">
              {{ log.returnValue ? log.returnValue.slice(0, 60) + (log.returnValue.length > 60 ? '…' : '') : '-' }}
            </td>
          </tr>
        </tbody>
      </table>
      <Pagination :page-no="pageNo" :page-size="pageSize" :total="total" @change="handlePageChange" />
    </div>
  </div>
</template>

<style scoped>
.chart-grid { margin-bottom: 16px; }
.log-card { margin-top: 0; }
.mono { font-family: Consolas, Monaco, monospace; font-size: 12px; }
.return-cell { max-width: 320px; color: var(--text-secondary); font-size: 12px; word-break: break-all; }
</style>
