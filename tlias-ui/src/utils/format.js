// 日期格式化工具函数

/**
 * 格式化为 yyyy-MM-dd
 * @param {string|Date} date - 后端 @JsonFormat 返回的日期字符串或 Date 对象
 * @returns {string} 格式化后的日期字符串，如 "2024-01-15"；null/undefined 返回 '-'
 */
export function formatDate(date) {
  if (!date) return '-'
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return '-'
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 格式化为 yyyy-MM-dd HH:mm:ss
 * @param {string|Date} date - 后端 @JsonFormat 返回的日期字符串或 Date 对象
 * @returns {string} 格式化后的日期时间字符串，如 "2024-01-15 14:30:00"；null/undefined 返回 '-'
 */
export function formatDateTime(date) {
  if (!date) return '-'
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return '-'
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}
