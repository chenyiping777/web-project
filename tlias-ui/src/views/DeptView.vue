<!-- 部门管理：列表展示 + 新增 / 修改 / 删除 -->
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listDept, addDept, updateDept, deleteDept } from '../api/dept'
import { formatDateTime } from '../utils/format'
import { toast } from '../utils/message'
import ModalDialog from '../components/ModalDialog.vue'

// 部门列表数据
const deptList = ref([])
// 加载中状态
const loading = ref(false)

// 弹窗状态与表单数据
const dialogVisible = ref(false)
const isEdit = ref(false) // 是否修改模式
const form = reactive({ id: null, name: '' })

// 加载部门列表
async function loadData() {
  loading.value = true
  try {
    // 拦截器已拆包，这里拿到的就是 data（部门数组）
    deptList.value = await listDept()
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
function openAdd() {
  isEdit.value = false
  form.id = null
  form.name = ''
  dialogVisible.value = true
}

// 打开修改弹窗（回显原名称）
function openEdit(row) {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  dialogVisible.value = true
}

// 提交表单：按模式调用新增 / 修改接口
async function handleSubmit() {
  // 前端校验：名称 2~10 位
  const name = form.name.trim()
  if (!name) { toast('请输入部门名称', 'error'); return }
  if (name.length < 2 || name.length > 10) { toast('部门名称长度需在 2~10 位之间', 'error'); return }

  if (isEdit.value) {
    await updateDept({ id: form.id, name })
    toast('修改成功', 'success')
  } else {
    await addDept({ name })
    toast('新增成功', 'success')
  }
  dialogVisible.value = false
  loadData() // 刷新列表
}

// 删除部门（带确认）
async function handleDelete(row) {
  if (!window.confirm(`确定删除部门「${row.name}」吗？`)) return
  await deleteDept(row.id)
  toast('删除成功', 'success')
  loadData()
}

// 页面加载时拉取数据
onMounted(loadData)
</script>

<template>
  <div class="card">
    <!-- 工具条 -->
    <div class="toolbar">
      <h3>部门管理</h3>
      <div class="spacer"></div>
      <button class="btn btn-primary" @click="openAdd">＋ 新增部门</button>
    </div>

    <!-- 部门表格 -->
    <table class="table">
      <thead>
        <tr>
          <th style="width: 80px">ID</th>
          <th>部门名称</th>
          <th>创建时间</th>
          <th>最后修改时间</th>
          <th style="width: 150px">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="5" class="empty">加载中...</td>
        </tr>
        <tr v-else-if="deptList.length === 0">
          <td colspan="5" class="empty">暂无数据</td>
        </tr>
        <tr v-for="d in deptList" :key="d.id">
          <td>{{ d.id }}</td>
          <td>{{ d.name }}</td>
          <td>{{ formatDateTime(d.createTime) }}</td>
          <td>{{ formatDateTime(d.updateTime) }}</td>
          <td>
            <button class="btn-link" @click="openEdit(d)">编辑</button>
            <button class="btn-link danger" @click="handleDelete(d)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增 / 修改弹窗 -->
    <ModalDialog v-model:visible="dialogVisible" :title="isEdit ? '修改部门' : '新增部门'">
      <div class="form-item">
        <span class="label required">部门名称</span>
        <div class="control">
          <input class="input" v-model.trim="form.name" placeholder="2~10 位字符" maxlength="10" />
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="dialogVisible = false">取消</button>
        <button class="btn btn-primary" @click="handleSubmit">确定</button>
      </template>
    </ModalDialog>
  </div>
</template>
