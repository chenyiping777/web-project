// 路由配置：登录页 + 主布局（侧边栏导航的五个业务页面）
import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '../api/auth'

// 布局组件：左侧导航 + 右侧内容区
import Layout from '../components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dept', // 默认进入部门管理
    children: [
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('../views/DeptView.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'emp',
        name: 'Emp',
        component: () => import('../views/EmpView.vue'),
        meta: { title: '员工管理' }
      },
      {
        path: 'clazz',
        name: 'Clazz',
        component: () => import('../views/ClazzView.vue'),
        meta: { title: '班级管理' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('../views/StudentView.vue'),
        meta: { title: '学员管理' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('../views/ReportView.vue'),
        meta: { title: '数据统计' }
      }
    ]
  }
]

// 使用 hash 模式，刷新页面不依赖后端路由支持
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫：未登录访问业务页面时，重定向到登录页
router.beforeEach((to, from, next) => {
  // 设置浏览器标签页标题
  document.title = to.meta.title ? `${to.meta.title} - tlias 教学管理系统` : 'tlias 教学管理系统'
  const token = getToken()
  if (to.path !== '/login' && !token) {
    next('/login') // 没有 token，去登录
  } else if (to.path === '/login' && token) {
    next('/') // 已登录还访问登录页，直接进首页
  } else {
    next()
  }
})

export default router
