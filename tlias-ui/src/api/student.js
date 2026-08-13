// 学员管理接口
import request from './request'

/**
 * 分页查询学员列表
 * GET /students/page，查询参数 StudentQuery：{ pageNo, pageSize, name, clazzId, degree }
 */
export function pageStudent(params) {
  return request.get('/students/page', { params })
}

/** 新增学员 POST /students（StudentDto） */
export function addStudent(data) {
  return request.post('/students', data)
}

/** 根据 ID 查询学员 GET /students/{id} */
export function getStudent(id) {
  return request.get(`/students/${id}`)
}

/** 修改学员 PUT /students（StudentDto，必须带 id；违纪处理也走此接口） */
export function updateStudent(data) {
  return request.put('/students', data)
}

/** 批量删除学员 DELETE /students/{ids}，多个 ID 用逗号分隔 */
export function deleteStudent(ids) {
  return request.delete(`/students/${ids}`)
}
