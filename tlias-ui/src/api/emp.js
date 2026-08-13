// 员工管理接口 + 文件上传
import request from './request'

/**
 * 分页查询员工列表（免 Token）
 * POST /emp/getAllEmp，请求体 EmpQuery：{ pageNo, pageSize, gender, deptName }
 */
export function pageEmp(data) {
  return request.post('/emp/getAllEmp', data)
}

/** 新增员工 POST /emp（EmpDto，可带 exprList 工作经历） */
export function addEmp(data) {
  return request.post('/emp', data)
}

/** 修改员工 PUT /emp（EmpDto，必须带 id） */
export function updateEmp(data) {
  return request.put('/emp', data)
}

/** 根据 ID 查询员工详情 GET /emp/{id} */
export function getEmp(id) {
  return request.get(`/emp/${id}`)
}

/** 批量删除员工 DELETE /emp/{ids}，多个 ID 用逗号分隔 */
export function deleteEmp(ids) {
  return request.delete(`/emp/${ids}`)
}

/**
 * 文件上传（返回 OSS 访问 URL）
 * POST /upload，multipart/form-data，字段名必须为 file
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  // axios 对 FormData 会自动设置 multipart 请求头
  return request.post('/upload', formData)
}
