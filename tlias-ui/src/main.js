// 应用入口：创建 Vue 应用实例，挂载路由与全局样式
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css' // 全局样式

const app = createApp(App)
app.use(router) // 注册路由
app.mount('#app') // 挂载到 index.html 的 #app 节点
