// Token 存取工具：登录成功后保存 JWT，请求拦截器自动携带
const TOKEN_KEY = 'tlias-token'

/** 读取本地保存的 Token */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 保存 Token（登录成功后调用） */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 清除 Token（退出登录 / 401 时调用） */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}
