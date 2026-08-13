<!-- 登录页：用户名 + 密码，成功后保存 token 并进入首页 -->
<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'
import { setToken } from '../api/auth'
import { toast } from '../utils/message'

const router = useRouter()

// 表单数据（默认填充测试账号，方便演示）
const form = reactive({
  username: '',
  password: ''
})

// 登录中状态，防止重复提交
const loading = ref(false)

// 提交登录
async function handleLogin() {
  // 前端基础校验
  if (!form.username.trim() || !form.password.trim()) {
    toast('请输入用户名和密码', 'error')
    return
  }
  loading.value = true
  try {
    // login 返回的 data 即 JWT Token（拦截器已拆包 Result）
    const token = await login({ ...form })
    setToken(token)          // 保存 token
    toast('登录成功', 'success')
    router.push('/')         // 进入首页
  } catch (e) {
    // 错误提示已由响应拦截器统一弹出，这里无需处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="title">tlias 教学管理系统</h2>
      <p class="subtitle">登录后进入管理后台</p>

      <!-- @submit.prevent 阻止表单默认刷新行为 -->
      <form @submit.prevent="handleLogin">
        <div class="field">
          <label>用户名</label>
          <input class="input" v-model.trim="form.username" placeholder="请输入用户名" />
        </div>
        <div class="field">
          <label>密码</label>
          <input class="input" v-model.trim="form.password" type="password" placeholder="请输入密码" />
        </div>
        <button class="btn btn-primary submit" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>

      <p class="tip">演示账号：zhangsan / 123456</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff87ad 0%, #ffb8cc 100%);
  /* 叠加卡通圆点背景，和全局页面背景呼应 */
  background-image:
    radial-gradient(#ffc8d8 12%, transparent 13%),
    radial-gradient(#ffc8d8 12%, transparent 13%);
  background-size: 45px 45px;
  background-position: 0 0, 22px 22px;
}
.login-card {
  width: 380px;
  background: #fffafc;
  border-radius: 22px;
  padding: 38px 34px;
  box-shadow: 0 10px 30px #ff9fb8;
  border: 3px solid #ffd6e0;
}
.title {
  text-align: center;
  font-size: 22px;
  margin-bottom: 6px;
  color: var(--primary-dark);
  font-weight:700;
}
.subtitle {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 28px;
}
.field {
  margin-bottom: 20px;
}
.field label {
  display: block;
  margin-bottom: 7px;
  color: var(--text-secondary);
  font-weight:500;
}
.field .input {
  width: 100%;
  height: 38px;
}
.submit {
  width: 100%;
  height: 44px;
  justify-content: center;
  font-size: 15px;
  margin-top: 6px;
}
.tip {
  margin-top: 20px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 12px;
}
</style>
