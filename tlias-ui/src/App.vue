<!-- 根组件：仅承载路由出口，并全局挂载轻提示 Toast -->
<script setup>
import Toast from './components/Toast.vue'
</script>

<template>
  <!-- 路由匹配到的页面渲染在这里 -->
  <router-view />
  <!-- 全局消息提示（成功/失败弹窗） -->
  <Toast />
</template>
