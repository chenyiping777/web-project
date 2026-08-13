import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置
// 开发服务器端口 5173；所有 /api 开头的请求被代理到后端 http://localhost:8080，
// 代理时通过 rewrite 去掉 /api 前缀，从而避免浏览器跨域问题。
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,               // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '') // /api/depts -> /depts
      }
    }
  }
})
