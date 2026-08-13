<!-- 主布局：左侧导航栏 + 顶栏 + 右侧内容区（router-view） -->
<script setup>
import { useRouter } from 'vue-router'
import { removeToken } from '../api/auth'

const router = useRouter()

// 左侧菜单项：path 对应路由路径
const menus = [
  { path: '/dept', name: '部门管理', icon: '🏢' },
  { path: '/emp', name: '员工管理', icon: '👥' },
  { path: '/clazz', name: '班级管理', icon: '📚' },
  { path: '/student', name: '学员管理', icon: '🎓' },
  { path: '/report', name: '数据统计', icon: '📊' }
]

// 退出登录：清除 token 并回到登录页
function logout() {
  removeToken()
  router.push('/login')
}
</script>

<template>
  <div class="layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo">tlias 教学管理</div>
      <nav>
        <!-- router-link 自动匹配激活样式 -->
        <router-link
          v-for="m in menus"
          :key="m.path"
          :to="m.path"
          class="menu-item"
        >
          <span class="icon">{{ m.icon }}</span>{{ m.name }}
        </router-link>
      </nav>
    </aside>

    <!-- 右侧主体 -->
    <div class="main">
      <header class="topbar">
        <span>欢迎使用 tlias 教学管理系统</span>
        <button class="btn" @click="logout">退出登录</button>
      </header>
      <div class="content">
        <!-- 子路由页面渲染处 -->
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
.layout { display: flex; height: 100%; }

.sidebar {
  width: 210px;
  flex-shrink: 0;
  background: #ffe8ef;
  color: var(--text);
  display: flex;
  flex-direction: column;
  border-right: 3px solid var(--border);
}

.logo {
  height: 64px;
  line-height: 64px;
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
  border-bottom: 2px solid var(--border);
}

.menu-item {
  display: block;
  margin: 6px 10px;
  padding: 12px 18px;
  border-radius: 14px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: #fffafc;
  transform: translateX(4px);
}

/* 激活菜单高亮，和全局主题色统一 */
.menu-item.router-link-active {
  color: #ffffff;
  background: var(--primary);
  box-shadow: 0 3px 0 var(--primary-dark);
}

.menu-item .icon {
  margin-right: 10px;
  font-size: 16px;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.topbar {
  height: 60px;
  flex-shrink: 0;
  background: #fffafc;
  border-bottom: 2px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  font-weight: 500;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}
</style>