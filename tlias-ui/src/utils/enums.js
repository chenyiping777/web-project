// 枚举字典：与后端 GenderEnum / JobEnum / DegreeEnum / SubjectEnum 保持一致
// 请求体 / 查询参数中需要传数字 code；后端响应统一返回中文描述

// 性别
export const GENDER = [
  { code: 1, label: '男' },
  { code: 2, label: '女' }
]

// 职位
export const JOB = [
  { code: 1, label: '班主任' },
  { code: 2, label: '讲师' },
  { code: 3, label: '学工主管' },
  { code: 4, label: '教学主管' },
  { code: 5, label: '校长' }
]

// 学历
export const DEGREE = [
  { code: 1, label: '初中' },
  { code: 2, label: '高中' },
  { code: 3, label: '大专' },
  { code: 4, label: '本科' },
  { code: 5, label: '硕士' },
  { code: 6, label: '博士' }
]

// 学科
export const SUBJECT = [
  { code: 1, label: '语文' },
  { code: 2, label: '数学' },
  { code: 3, label: '英语' },
  { code: 4, label: '物理' },
  { code: 5, label: '化学' },
  { code: 6, label: '生物' }
]

// 班级状态（ClazzVo.status）
export const CLAZZ_STATUS = [
  { code: 0, label: '未开班' },
  { code: 1, label: '已开班' },
  { code: 2, label: '结课' }
]

/**
 * 根据 code 取中文标签
 * @param {Array} list 枚举数组
 * @param {number} code 编码
 */
export function labelOf(list, code) {
  const item = list.find((i) => i.code === code)
  return item ? item.label : code
}

/**
 * 根据中文标签反查 code（用于修改回显，部分详情接口返回中文）
 * @param {Array} list 枚举数组
 * @param {string|number} value 中文标签或 code
 */
export function codeOf(list, value) {
  if (typeof value === 'number') return value
  const item = list.find((i) => i.label === value)
  return item ? item.code : null
}
