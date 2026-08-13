<!-- 通用分页组件
     属性：pageNo 当前页 / pageSize 每页条数 / total 总记录数
     事件：change(pageNo) 页码变化（点击上一页/下一页/页码时触发） -->
<script setup>
import { computed } from 'vue'

const props = defineProps({
  pageNo: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  total: { type: Number, default: 0 }
})

const emit = defineEmits(['change'])

// 总页数（至少 1 页）
const pages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

// 生成页码按钮列表（最多显示 7 个页码，超出用省略号）
const pageNumbers = computed(() => {
  const total = pages.value
  const cur = props.pageNo
  const nums = []
  if (total <= 7) {
    // 页数少，全部显示
    for (let i = 1; i <= total; i++) nums.push(i)
  } else {
    // 固定结构：1 ... 中间 5 个 ... 末页
    let start = Math.max(2, cur - 2)
    let end = Math.min(total - 1, cur + 2)
    if (cur <= 3) { start = 2; end = 6 }
    if (cur >= total - 2) { start = total - 5; end = total - 1 }
    nums.push(1)
    if (start > 2) nums.push('...')
    for (let i = start; i <= end; i++) nums.push(i)
    if (end < total - 1) nums.push('...')
    nums.push(total)
  }
  return nums
})

// 跳转页码（做边界保护）
function go(page) {
  if (page < 1 || page > pages.value || page === props.pageNo) return
  emit('change', page)
}
</script>

<template>
  <div class="pager">
    <span class="info">共 {{ total }} 条</span>
    <button :disabled="pageNo <= 1" @click="go(pageNo - 1)">上一页</button>
    <!-- 页码按钮，字符串 '...' 渲染为省略号 -->
    <template v-for="(p, i) in pageNumbers" :key="i">
      <button v-if="p === '...'" disabled>…</button>
      <button v-else :class="{ active: p === pageNo }" @click="go(p)">{{ p }}</button>
    </template>
    <button :disabled="pageNo >= pages" @click="go(pageNo + 1)">下一页</button>
  </div>
</template>
