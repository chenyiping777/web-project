// 轻量级消息提示：用响应式数组驱动 Toast.vue 渲染
import { reactive } from 'vue'

// 全局消息列表（Toast 组件监听该数组）
export const messages = reactive([])

let seed = 0

/**
 * 弹出一条提示
 * @param {string} text 文本
 * @param {'success'|'error'|'info'} type 类型
 */
export function toast(text, type = 'info') {
  const id = ++seed
  messages.push({ id, text, type })
  // 2.6 秒后自动移除
  setTimeout(() => {
    const idx = messages.findIndex((m) => m.id === id)
    if (idx !== -1) messages.splice(idx, 1)
  }, 2600)
}
