<!-- 班级管理：条件查询（学科 / 开课时间区间）+ 分页 + 新增 / 修改 / 删除 -->
<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { pageClazz, getClazz, addClazz, updateClazz, deleteClazz } from '../api/clazz'
import { pageEmp } from '../api/emp'
import { SUBJECT, CLAZZ_STATUS, codeOf, labelOf } from '../utils/enums'
import { formatDate, formatDateTime } from '../utils/format'
import { toast } from '../utils/message'
import ModalDialog from '../components/ModalDialog.vue'
import Pagination from '../components/Pagination.vue'

/* ---------------- 列表与查询 ---------------- */

// 查询条件（ClazzQuery：subject 数字编码、beginDate/endDate 开课时间区间 yyyy-MM-dd）
const query = reactive({ subject: null, beginDate: '', endDate: '' })
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const clazzList = ref([])
const loading = ref(false)

// 班主任下拉数据（复用员工分页接口，取前 200 条）
const empOptions = ref([])

// 班级状态标签样式映射：0 未开班 / 1 已开班 / 2 结课
const statusTagClass = computed(() => ({ 0: 'tag-gray', 1: 'tag-green', 2: 'tag-blue' }))

// 加载班级分页列表
async function loadData() {
  loading.value = true
  try {
    // GET /clazz?pageNo&pageSize&subject&beginDate&endDate
    const data = await pageClazz({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      subject: query.subject || undefined,
      beginDate: query.beginDate || undefined,
      endDate: query.endDate || undefined
    })
    clazzList.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNo.value = 1
  loadData()
}

function handleReset() {
  query.subject = null
  query.beginDate = ''
  query.endDate = ''
  handleSearch()
}

function handlePageChange(p) {
  pageNo.value = p
  loadData()
}

/* ---------------- 删除 ---------------- */

async function handleDelete(row) {
  if (!window.confirm(`确定删除班级「${row.name}」吗？`)) return
  await deleteClazz(row.id)
  toast('删除成功', 'success')
  loadData()
}

/* ---------------- 新增 / 修改弹窗 ---------------- */

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

function emptyForm() {
  return {
    id: null,
    name: '',
    room: '',
    subject: null,   // 学科编码
    beginDate: '',   // yyyy-MM-dd
    endDate: '',
    masterId: null   // 班主任（员工）ID
  }
}
const form = reactive(emptyForm())

function openAdd() {
  isEdit.value = false
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

// 修改回显：GET /clazz/{id}
async function openEdit(row) {
  isEdit.value = true
  const d = await getClazz(row.id)
  Object.assign(form, emptyForm(), {
    id: d.id,
    name: d.name || '',
    room: d.room || '',
    // 详情接口学科可能返回中文，统一反查成编码
    subject: codeOf(SUBJECT, d.subject),
    beginDate: formatDate(d.beginDate),
    endDate: formatDate(d.endDate),
    masterId: d.masterId ?? null
  })
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!form.name.trim()) { toast('请输入班级名称', 'error'); return }
  if (!form.room.trim()) { toast('请输入教室', 'error'); return }
  if (!form.beginDate || !form.endDate) { toast('请选择开课和结课日期', 'error'); return }
  if (form.beginDate > form.endDate) { toast('开课日期不能晚于结课日期', 'error'); return }

  submitting.value = true
  try {
    if (isEdit.value) {
      // 修改必须携带 id 与 masterId（masterId 为空时给 0 占位，后端要求非空）
      await updateClazz({ ...form, masterId: form.masterId ?? 0 })
      toast('修改成功', 'success')
    } else {
      const { id, ...body } = form
      await addClazz(body)
      toast('新增成功', 'success')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

/* ---------------- 初始化 ---------------- */

onMounted(async () => {
  // 预加载班主任下拉（员工分页接口免 Token，id 字段已在后端补齐）
  try {
    const data = await pageEmp({ pageNo: 1, pageSize: 200 })
    empOptions.value = data.list || []
  } catch (e) { /* 下拉加载失败不影响页面主体 */ }
  loadData()
})
</script>

<template>
  <div class="card">
    <!-- 查询工具条 -->
    <div class="toolbar">
      <select class="select" v-model="query.subject">
        <option :value="null">全部学科</option>
        <option v-for="s in SUBJECT" :key="s.code" :value="s.code">{{ s.label }}</option>
      </select>
      <input class="input date-input" type="date" v-model="query.beginDate" title="开课开始日期" />
      <span class="sep">~</span>
      <input class="input date-input" type="date" v-model="query.endDate" title="开课结束日期" />
      <button class="btn btn-primary" @click="handleSearch">查询</button>
      <button class="btn" @click="handleReset">重置</button>
      <div class="spacer"></div>
      <button class="btn btn-primary" @click="openAdd">＋ 新增班级</button>
    </div>

    <!-- 班级表格 -->
    <table class="table">
      <thead>
        <tr>
          <th>班级名称</th>
          <th>学科</th>
          <th>班主任</th>
          <th>教室</th>
          <th>开课日期</th>
          <th>结课日期</th>
          <th>状态</th>
          <th>创建时间</th>
          <th style="width: 120px">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="9" class="empty">加载中...</td>
        </tr>
        <tr v-else-if="clazzList.length === 0">
          <td colspan="9" class="empty">暂无数据</td>
        </tr>
        <tr v-for="c in clazzList" :key="c.id">
          <td>{{ c.name }}</td>
          <td>{{ c.subject }}</td>
          <td>{{ c.masterName || '-' }}</td>
          <td>{{ c.room || '-' }}</td>
          <td>{{ formatDate(c.beginDate) }}</td>
          <td>{{ formatDate(c.endDate) }}</td>
          <td>
            <!-- 后端返回 status：0 未开班 / 1 已开班 / 2 结课 -->
            <span class="tag" :class="statusTagClass[c.status]">{{ labelOf(CLAZZ_STATUS, c.status) }}</span>
          </td>
          <td>{{ formatDateTime(c.createTime) }}</td>
          <td>
            <button class="btn-link" @click="openEdit(c)">编辑</button>
            <button class="btn-link danger" @click="handleDelete(c)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <Pagination :page-no="pageNo" :page-size="pageSize" :total="total" @change="handlePageChange" />

    <!-- 新增 / 修改弹窗 -->
    <ModalDialog v-model:visible="dialogVisible" :title="isEdit ? '修改班级' : '新增班级'">
      <div class="form-item">
        <span class="label required">班级名称</span>
        <div class="control"><input class="input" v-model.trim="form.name" placeholder="如：JavaEE就业168期" /></div>
      </div>
      <div class="form-item">
        <span class="label required">教室</span>
        <div class="control"><input class="input" v-model.trim="form.room" placeholder="如：101教室" /></div>
      </div>
      <div class="form-item">
        <span class="label">学科</span>
        <div class="control">
          <select class="select" v-model="form.subject">
            <option :value="null">请选择学科</option>
            <option v-for="s in SUBJECT" :key="s.code" :value="s.code">{{ s.label }}</option>
          </select>
        </div>
      </div>
      <div class="form-item">
        <span class="label required">开课日期</span>
        <div class="control"><input class="input" type="date" v-model="form.beginDate" /></div>
      </div>
      <div class="form-item">
        <span class="label required">结课日期</span>
        <div class="control"><input class="input" type="date" v-model="form.endDate" /></div>
      </div>
      <div class="form-item">
        <span class="label">班主任</span>
        <div class="control">
          <select class="select" v-model="form.masterId">
            <option :value="null">请选择班主任</option>
            <option v-for="e in empOptions" :key="e.id" :value="e.id">{{ e.name }}</option>
          </select>
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="dialogVisible = false">取消</button>
        <button class="btn btn-primary" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '确定' }}
        </button>
      </template>
    </ModalDialog>
  </div>
</template>

<style scoped>
.date-input { width: 160px; }
.sep { color: var(--text-secondary); }
</style>
