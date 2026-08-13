<!-- 全局 Toast 轻提示：读取 message.js 中的响应式消息列表 -->
<script setup>
import { messages } from '../utils/message'
</script>

<template>
  <div class="toast-wrap">
    <!-- 遍历消息列表逐条渲染，2.6 秒后由 message.js 自动移除 -->
    <div v-for="m in messages" :key="m.id" class="toast" :class="m.type">
      <span class="dot"></span>
      <span>{{ m.text }}</span>
    </div>
  </div>
</template>
