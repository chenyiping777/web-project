<!-- 学员管理：条件查询（姓名 / 班级 / 学历）+ 分页 + 新增 / 修改（含违纪扣分）+ 批量删除 -->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageStudent, addStudent, updateStudent, getStudent, deleteStudent } from '../api/student'
import { listAllClazz } from '../api/clazz'
import { GENDER, DEGREE, codeOf } from '../utils/enums'
import { formatDate, formatDateTime } from '../utils/format'
import { toast } from '../utils/message'
import ModalDialog from '../components/ModalDialog.vue'
import Pagination from '../components/Pagination.vue'

/* ---------------- 列表与查询 ---------------- */

// 查询条件（StudentQuery：name 精确匹配、clazzId、degree 数字编码）
const query = reactive({ name: '', clazzId: null, degree: null })
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const studentList = ref([])
const loading = ref(false)
const selectedIds = ref([])

// 班级下拉数据（GET /clazz/all）
const clazzOptions = ref([])

// clazzId -> 班级名称 映射，用于列表中展示班级
function clazzNameOf(id) {
  const c = clazzOptions.value.find((i) => i.id === id)
  return c ? c.name : '-'
}

// 加载学员分页列表
async function loadData() {
  loading.value = true
  try {
    // GET /students/page?pageNo&pageSize&name&clazzId&degree
    const data = await pageStudent({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      name: query.name.trim() || undefined,
      clazzId: query.clazzId || undefined,
      degree: query.degree || undefined
    })
    studentList.value = data.list || []
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
  query.name = ''
  query.clazzId = null
  query.degree = null
  handleSearch()
}

function handlePageChange(p) {
  pageNo.value = p
  loadData()
}

/* ---------------- 多选 / 删除 ---------------- */

function toggleSelectAll(e) {
  selectedIds.value = e.target.checked ? studentList.value.map((i) => i.id) : []
}

function toggleSelect(id, checked) {
  if (checked) {
    if (!selectedIds.value.includes(id)) selectedIds.value.push(id)
  } else {
    selectedIds.value = selectedIds.value.filter((i) => i !== id)
  }
}

// 批量删除：DELETE /students/{ids}
async function handleBatchDelete() {
  if (selectedIds.value.length === 0) { toast('请先勾选要删除的学员', 'error'); return }
  if (!window.confirm(`确定删除选中的 ${selectedIds.value.length} 名学员吗？`)) return
  await deleteStudent(selectedIds.value.join(','))
  toast('删除成功', 'success')
  selectedIds.value = []
  loadData()
}

// 单行删除
async function handleDeleteOne(row) {
  if (!window.confirm(`确定删除学员「${row.name}」吗？`)) return
  await deleteStudent(row.id)
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
    no: '',             // 学号
    gender: 1,          // 性别编码
    phone: '',
    idCard: '',
    isCollege: false,   // 是否来自院校
    address: '',
    degree: null,       // 学历编码
    graduationDate: '', // yyyy-MM-dd
    clazzId: null,      // 所属班级
    violationCount: 0,  // 违纪次数
    violationScore: 0   // 违纪扣分
  }
}
const form = reactive(emptyForm())

function openAdd() {
  isEdit.value = false
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

// 修改回显：GET /students/{id}
// 注意：详情接口返回实体，gender 为数字、isCollege 为 1/0、degree 为中文，需分别处理
async function openEdit(row) {
  isEdit.value = true
  const d = await getStudent(row.id)
  Object.assign(form, emptyForm(), {
    id: d.id,
    name: d.name || '',
    no: d.no || '',
    gender: typeof d.gender === 'number' ? d.gender : (codeOf(GENDER, d.gender) ?? 1),
    phone: d.phone || '',
    idCard: d.idCard || '',
    // 1/0 或 true/false 统一转布尔
    isCollege: d.isCollege === 1 || d.isCollege === true,
    address: d.address || '',
    degree: codeOf(DEGREE, d.degree),
    graduationDate: formatDate(d.graduationDate),
    clazzId: d.clazzId ?? null,
    violationCount: d.violationCount ?? 0,
    violationScore: d.violationScore ?? 0
  })
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!form.name.trim()) { toast('请输入姓名', 'error'); return }
  if (!form.no.trim()) { toast('请输入学号', 'error'); return }
  if (!form.phone.trim()) { toast('请输入手机号', 'error'); return }
  if (!form.degree) { toast('请选择学历', 'error'); return }
  if (!form.clazzId) { toast('请选择所属班级', 'error'); return }

  submitting.value = true
  try {
    if (isEdit.value) {
      // 修改必须携带 id、violationCount、violationScore（违纪处理也走本接口）
      await updateStudent({ ...form })
      toast('修改成功', 'success')
    } else {
      const { id, ...body } = form
      await addStudent(body)
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
  // 班级下拉与列表并行加载
  try {
    clazzOptions.value = await listAllClazz()
  } catch (e) { /* 下拉加载失败不影响页面主体 */ }
  loadData()
})
</script>

<template>
  <div class="card">
    <!-- 查询工具条 -->
    <div class="toolbar">
      <input class="input" v-model.trim="query.name" placeholder="学员姓名（精确匹配）" @keyup.enter="handleSearch" />
      <select class="select" v-model="query.clazzId">
        <option :value="null">全部班级</option>
        <option v-for="c in clazzOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select class="select" v-model="query.degree">
        <option :value="null">全部学历</option>
        <option v-for="d in DEGREE" :key="d.code" :value="d.code">{{ d.label }}</option>
      </select>
      <button class="btn btn-primary" @click="handleSearch">查询</button>
      <button class="btn" @click="handleReset">重置</button>
      <div class="spacer"></div>
      <button class="btn btn-danger" @click="handleBatchDelete">批量删除</button>
      <button class="btn btn-primary" @click="openAdd">＋ 新增学员</button>
    </div>

    <!-- 学员表格 -->
    <table class="table">
      <thead>
        <tr>
          <th style="width: 40px">
            <input
              type="checkbox"
              :checked="studentList.length > 0 && selectedIds.length === studentList.length"
              @change="toggleSelectAll"
            />
          </th>
          <th>学号</th>
          <th>姓名</th>
          <th>性别</th>
          <th>手机号</th>
          <th>学历</th>
          <th>所属班级</th>
          <th>违纪次数</th>
          <th>违纪扣分</th>
          <th style="width: 120px">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="10" class="empty">加载中...</td>
        </tr>
        <tr v-else-if="studentList.length === 0">
          <td colspan="10" class="empty">暂无数据</td>
        </tr>
        <tr v-for="s in studentList" :key="s.id">
          <td>
            <input
              type="checkbox"
              :checked="selectedIds.includes(s.id)"
              @change="toggleSelect(s.id, $event.target.checked)"
            />
          </td>
          <td>{{ s.no }}</td>
          <td>{{ s.name }}</td>
          <td>{{ s.gender }}</td>
          <td>{{ s.phone }}</td>
          <td>{{ s.degree }}</td>
          <td>{{ clazzNameOf(s.clazzId) }}</td>
          <td>{{ s.violationCount ?? 0 }}</td>
          <!-- 违纪扣分大于 0 时红色高亮提示 -->
          <td>
            <span :class="{ 'score-warn': s.violationScore > 0 }">-{{ s.violationScore ?? 0 }}</span>
          </td>
          <td>
            <button class="btn-link" @click="openEdit(s)">编辑</button>
            <button class="btn-link danger" @click="handleDeleteOne(s)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <Pagination :page-no="pageNo" :page-size="pageSize" :total="total" @change="handlePageChange" />

    <!-- 新增 / 修改弹窗 -->
    <ModalDialog v-model:visible="dialogVisible" :title="isEdit ? '修改学员' : '新增学员'" large>
      <div class="form-item">
        <span class="label required">姓名</span>
        <div class="control"><input class="input" v-model.trim="form.name" placeholder="学员姓名" /></div>
      </div>
      <div class="form-item">
        <span class="label required">学号</span>
        <div class="control"><input class="input" v-model.trim="form.no" placeholder="如：2022000001" /></div>
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
        <span class="label">身份证号</span>
        <div class="control"><input class="input" v-model.trim="form.idCard" placeholder="18 位身份证号" /></div>
      </div>
      <div class="form-item">
        <span class="label">是否院校</span>
        <div class="control radio-group">
          <label><input type="checkbox" v-model="form.isCollege" /> 来自院校</label>
        </div>
      </div>
      <div class="form-item">
        <span class="label">联系地址</span>
        <div class="control"><input class="input" v-model.trim="form.address" placeholder="联系地址" /></div>
      </div>
      <div class="form-item">
        <span class="label required">学历</span>
        <div class="control">
          <select class="select" v-model="form.degree">
            <option :value="null">请选择学历</option>
            <option v-for="d in DEGREE" :key="d.code" :value="d.code">{{ d.label }}</option>
          </select>
        </div>
      </div>
      <div class="form-item">
        <span class="label">毕业时间</span>
        <div class="control"><input class="input" type="date" v-model="form.graduationDate" /></div>
      </div>
      <div class="form-item">
        <span class="label required">所属班级</span>
        <div class="control">
          <select class="select" v-model="form.clazzId">
            <option :value="null">请选择班级</option>
            <option v-for="c in clazzOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
      </div>

      <!-- 违纪处理区域：违纪次数与扣分通过修改学员接口更新 -->
      <div class="violation-block">
        <div class="violation-title">违纪处理</div>
        <div class="form-item">
          <span class="label">违纪次数</span>
          <div class="control"><input class="input" type="number" min="0" v-model.number="form.violationCount" /></div>
        </div>
        <div class="form-item">
          <span class="label">违纪扣分</span>
          <div class="control"><input class="input" type="number" min="0" v-model.number="form.violationScore" /></div>
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
.radio-group { display: flex; gap: 18px; line-height: 34px; }
.radio-group label { display: flex; align-items: center; gap: 4px; cursor: pointer; }
.score-warn { color: var(--danger); font-weight: 600; }
.violation-block {
  border: 1px dashed var(--border); border-radius: 8px;
  padding: 12px; margin-top: 4px;
}
.violation-title { font-weight: 600; margin-bottom: 10px; }
.violation-block .form-item:last-child { margin-bottom: 0; }
</style>
