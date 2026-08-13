// axios 统一封装：
// 1. 基础地址 /api（由 vite 代理转发到后端 8080，并去掉 /api 前缀）
// 2. 请求拦截器：自动在请求头携带 token（后端要求放在名为 token 的请求头中）
// 3. 响应拦截器：统一拆包后端 Result 结构 {code,msg,data}，并处理 401 未登录
import axios from 'axios'
import { getToken, removeToken } from './auth'
import { toast } from '../utils/message'
import router from '../router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',  // 所有请求以 /api 开头，交给 vite 代理
  timeout: 10000    // 超时 10 秒
})

// 请求拦截器：注入 token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 后端 TokenInterceptor 读取的是名为 "token" 的请求头
      config.headers['token'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：拆包 Result / 处理 401
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回 Result：code===1 表示成功
    if (res.code === 1) {
      // 成功时直接把 data 交给业务层，简化调用
      return res.data
    }
    // 业务失败（code===0），弹出后端返回的提示信息
    toast(res.msg || '操作失败', 'error')
    return Promise.reject(new Error(res.msg || '操作失败'))
  },
  (error) => {
    // HTTP 401：仅由登录拦截器在 token 缺失/非法时返回
    if (error.response && error.response.status === 401) {
      removeToken()
      toast('未登录或登录已过期，请重新登录', 'error')
      // 跳回登录页（避免重复跳转）
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    } else {
      toast(error.message || '网络异常，请稍后重试', 'error')
    }
    return Promise.reject(error)
  }
)

export default request
