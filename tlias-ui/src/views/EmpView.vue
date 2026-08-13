<!-- 员工管理：条件查询 + 分页 + 新增 / 修改（含头像上传、工作经历）+ 批量删除 -->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageEmp, addEmp, updateEmp, getEmp, deleteEmp, uploadFile } from '../api/emp'
import { listDept } from '../api/dept'
import { GENDER, JOB, codeOf } from '../utils/enums'
import { formatDate, formatDateTime } from '../utils/format'
import { toast } from '../utils/message'
import ModalDialog from '../components/ModalDialog.vue'
import Pagination from '../components/Pagination.vue'

/* ---------------- 列表与查询 ---------------- */

// 查询条件（EmpQuery：gender 数字编码、deptName 模糊匹配）
const query = reactive({ gender: null, deptName: '' })
// 分页状态
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 表格数据与加载状态
const empList = ref([])
const loading = ref(false)
// 勾选的员工 ID 集合（批量删除用）
const selectedIds = ref([])

// 部门下拉数据（表单中的所属部门选择）
const deptOptions = ref([])

// 加载员工分页列表
async function loadData() {
  loading.value = true
  try {
    // POST /emp/getAllEmp，请求体携带分页与筛选条件
    const data = await pageEmp({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      gender: query.gender || undefined,
      deptName: query.deptName.trim() || undefined
    })
    empList.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

// 条件查询：回到第 1 页重新加载
function handleSearch() {
  pageNo.value = 1
  loadData()
}

// 重置查询条件
function handleReset() {
  query.gender = null
  query.deptName = ''
  handleSearch()
}

// 分页组件页码变化
function handlePageChange(p) {
  pageNo.value = p
  loadData()
}

/* ---------------- 全选 / 多选 ---------------- */

// 表头全选切换
function toggleSelectAll(e) {
  selectedIds.value = e.target.checked ? empList.value.map((i) => i.id) : []
}

// 单行勾选切换
function toggleSelect(id, checked) {
  if (checked) {
    if (!selectedIds.value.includes(id)) selectedIds.value.push(id)
  } else {
    selectedIds.value = selectedIds.value.filter((i) => i !== id)
  }
}

// 批量删除
async function handleBatchDelete() {
  if (selectedIds.value.length === 0) { toast('请先勾选要删除的员工', 'error'); return }
  if (!window.confirm(`确定删除选中的 ${selectedIds.value.length} 名员工吗？`)) return
  // DELETE /emp/{ids}，多个 ID 逗号拼接
  await deleteEmp(selectedIds.value.join(','))
  toast('删除成功', 'success')
  selectedIds.value = []
  loadData()
}

// 单行删除（复用批量删除接口，传单个 ID）
async function handleDeleteOne(row) {
  if (!window.confirm(`确定删除员工「${row.name}」吗？`)) return
  await deleteEmp(row.id)
  toast('删除成功', 'success')
  loadData()
}

/* ---------------- 新增 / 修改弹窗 ---------------- */

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

// 空表单工厂函数
function emptyForm() {
  return {
    id: null,
    username: '',
    name: '',
    gender: 1,          // 性别编码，默认男
    phone: '',
    deptId: null,
    job: null,          // 职位编码
    salary: null,
    entryDate: '',      // yyyy-MM-dd
    image: '',          // 头像 URL
    experList: []       // 工作经历（请求字段名为 experList）
  }
}
const form = reactive(emptyForm())

// 打开新增弹窗
function openAdd() {
  isEdit.value = false
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

// 打开修改弹窗：先调详情接口回显
async function openEdit(row) {
  isEdit.value = true
  const d = await getEmp(row.id) // GET /emp/{id}
  Object.assign(form, emptyForm(), {
    id: d.id,
    username: d.username || '',
    name: d.name || '',
    // 详情接口性别/职位返回中文，反查为编码供下拉/单选使用
    gender: codeOf(GENDER, d.gender) ?? 1,
    phone: d.phone || '',
    job: codeOf(JOB, d.job),
    salary: d.salary,
    entryDate: formatDate(d.entryDate),
    image: d.image || '',
    deptId: null,
    // 响应字段名为 exprList（历史拼写），做兼容处理
    experList: (d.exprList || d.experList || []).map((e) => ({
      begin: formatDate(e.begin),
      end: formatDate(e.end),
      company: e.company || '',
      job: e.job || ''
    }))
  })
  // 详情接口不返回 deptId，用部门名称匹配出 ID 供下拉选中
  if (d.deptName) {
    const hit = deptOptions.value.find((x) => x.name === d.deptName)
    if (hit) form.deptId = hit.id
  }
  dialogVisible.value = true
}

// 工作经历：添加一行 / 删除一行
function addExper() {
  form.experList.push({ begin: '', end: '', company: '', job: '' })
}
function removeExper(index) {
  form.experList.splice(index, 1)
}

// 头像上传：选择文件后调用 /upload，返回 OSS URL 存入 form.image
async function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  // 简单的类型与大小校验
  if (!file.type.startsWith('image/')) { toast('请选择图片文件', 'error'); return }
  if (file.size > 10 * 1024 * 1024) { toast('图片不能超过 10MB', 'error'); return }
  try {
    form.image = await uploadFile(file) // 返回文件访问 URL
    toast('头像上传成功', 'success')
  } catch (err) {
    // 上传失败提示已由拦截器弹出
  } finally {
    e.target.value = '' // 清空 input，允许重复选择同一文件
  }
}

// 提交表单
async function handleSubmit() {
  // 前端基础校验（后端还有 @Valid 校验兜底）
  if (!form.username.trim()) { toast('请输入用户名', 'error'); return }
  if (!form.name.trim()) { toast('请输入姓名', 'error'); return }
  if (!form.phone.trim()) { toast('请输入手机号', 'error'); return }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateEmp({ ...form })
      toast('修改成功', 'success')
    } else {
      // 新增时不传 id
      const { id, ...body } = form
      await addEmp(body)
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
  // 部门下拉与列表并行加载
  deptOptions.value = await listDept()
  loadData()
})
</script>

<template>
  <div class="card">
    <!-- 查询工具条 -->
    <div class="toolbar">
      <select class="select" v-model="query.gender">
        <option :value="null">全部性别</option>
        <option v-for="g in GENDER" :key="g.code" :value="g.code">{{ g.label }}</option>
      </select>
      <input class="input" v-model.trim="query.deptName" placeholder="部门名称（模糊查询）" @keyup.enter="handleSearch" />
      <button class="btn btn-primary" @click="handleSearch">查询</button>
      <button class="btn" @click="handleReset">重置</button>
      <div class="spacer"></div>
      <button class="btn btn-danger" @click="handleBatchDelete">批量删除</button>
      <button class="btn btn-primary" @click="openAdd">＋ 新增员工</button>
    </div>

    <!-- 员工表格 -->
    <table class="table">
      <thead>
        <tr>
          <th style="width: 40px">
            <!-- 全选框：勾选行数等于当前页行数视为全选 -->
            <input
              type="checkbox"
              :checked="empList.length > 0 && selectedIds.length === empList.length"
              @change="toggleSelectAll"
            />
          </th>
          <th>头像</th>
          <th>姓名</th>
          <th>性别</th>
          <th>职位</th>
          <th>所属部门</th>
          <th>入职日期</th>
          <th>最后修改时间</th>
          <th style="width: 120px">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="9" class="empty">加载中...</td>
        </tr>
        <tr v-else-if="empList.length === 0">
          <td colspan="9" class="empty">暂无数据</td>
        </tr>
        <tr v-for="emp in empList" :key="emp.id">
          <td>
            <input
              type="checkbox"
              :checked="selectedIds.includes(emp.id)"
              @change="toggleSelect(emp.id, $event.target.checked)"
            />
          </td>
          <td>
            <!-- 头像：http 地址直接展示，否则显示姓名首字占位 -->
            <img v-if="emp.image && emp.image.startsWith('http')" class="avatar" :src="emp.image" />
            <span v-else class="avatar avatar-placeholder">{{ (emp.name || '?').slice(0, 1) }}</span>
          </td>
          <td>{{ emp.name }}</td>
          <td>{{ emp.gender }}</td>
          <td>{{ emp.job || '-' }}</td>
          <td>{{ emp.deptName || '-' }}</td>
          <td>{{ formatDate(emp.entryDate) }}</td>
          <td>{{ formatDateTime(emp.updateTime) }}</td>
          <td>
            <button class="btn-link" @click="openEdit(emp)">编辑</button>
            <button class="btn-link danger" @click="handleDeleteOne(emp)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 分页 -->
    <Pagination :page-no="pageNo" :page-size="pageSize" :total="total" @change="handlePageChange" />

    <!-- 新增 / 修改弹窗 -->
    <ModalDialog v-model:visible="dialogVisible" :title="isEdit ? '修改员工' : '新增员工'" large>
      <div class="form-item">
        <span class="label required">用户名</span>
        <div class="control"><input class="input" v-model.trim="form.username" placeholder="登录用户名" /></div>
      </div>
      <div class="form-item">
        <span class="label required">姓名</span>
        <div class="control"><input class="input" v-model.trim="form.name" placeholder="员工姓名" /></div>
      </div>
      <div class="form-item">
        <span class="label required">性别</span>
        <div class="control radio-group">
          <label v-for="g in GENDER" :key="g.code">
            <input type="radio" :value="g.code" v-model="form.gender" /> {{ g.label }}
          </label>
        </div>
      </div>
      <div class="form-item">
        <span class="label required">手机号</span>
        <div class="control"><input class="input" v-model.trim="form.phone" placeholder="11 位手机号" /></div>
      </div>
      <div class="form-item">
        <span class="label">所属部门</span>
        <div class="control">
          <select class="select" v-model="form.deptId">
            <option :value="null">请选择部门</option>
            <option v-for="d in deptOptions" :key="d.id" :value="d.id">{{ d.name }}</option>
          </select>
        </div>
      </div>
      <div class="form-item">
        <span class="label">职位</span>
        <div class="control">
          <select class="select" v-model="form.job">
            <option :value="null">请选择职位</option>
            <option v-for="j in JOB" :key="j.code" :value="j.code">{{ j.label }}</option>
          </select>
        </div>
      </div>
      <div class="form-item">
        <span class="label">薪资</span>
        <div class="control"><input class="input" type="number" v-model.number="form.salary" placeholder="月薪（元）" /></div>
      </div>
      <div class="form-item">
        <span class="label">入职日期</span>
        <div class="control"><input class="input" type="date" v-model="form.entryDate" /></div>
      </div>
      <div class="form-item">
        <span class="label">头像</span>
        <div class="control upload-row">
          <!-- 选择文件后自动上传 OSS 并回填 URL -->
          <input type="file" accept="image/*" @change="handleAvatarChange" />
          <img v-if="form.image && form.image.startsWith('http')" class="avatar" :src="form.image" />
          <span v-else-if="form.image" class="url-text">{{ form.image }}</span>
        </div>
      </div>

      <!-- 工作经历动态表单 -->
      <div class="exper-block">
        <div class="exper-head">
          <span>工作经历</span>
          <button class="btn" @click="addExper">＋ 添加一段</button>
        </div>
        <div v-if="form.experList.length === 0" class="exper-empty">暂无，可点击上方添加</div>
        <div v-for="(exp, i) in form.experList" :key="i" class="exper-row">
          <input class="input" type="date" v-model="exp.begin" title="开始日期" />
          <span class="sep">~</span>
          <input class="input" type="date" v-model="exp.end" title="结束日期" />
          <input class="input" v-model.trim="exp.company" placeholder="公司名称" />
          <input class="input" v-model.trim="exp.job" placeholder="职位" />
          <button class="btn-link danger" @click="removeExper(i)">删除</button>
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
.avatar-placeholder {
  display: inline-flex; align-items: center; justify-content: center;
  background: #e8efff; color: var(--primary); font-weight: 600;
}
.radio-group { display: flex; gap: 18px; line-height: 34px; }
.radio-group label { display: flex; align-items: center; gap: 4px; cursor: pointer; }
.upload-row { display: flex; align-items: center; gap: 12px; }
.url-text { color: var(--text-secondary); font-size: 12px; word-break: break-all; }

.exper-block { border: 1px dashed var(--border); border-radius: 8px; padding: 12px; }
.exper-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-weight: 600; }
.exper-empty { color: var(--text-secondary); font-size: 13px; }
.exper-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.exper-row .input { flex: 1; width: auto; }
.exper-row .sep { color: var(--text-secondary); }
</style>
