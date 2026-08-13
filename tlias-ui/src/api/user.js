// 用户相关接口：登录
import request from './request'

/**
 * 用户登录
 * POST /user/login  { username, password }
 * 成功时返回 JWT Token 字符串
 */
export function login(data) {
  return request.post('/user/login', data)
}
