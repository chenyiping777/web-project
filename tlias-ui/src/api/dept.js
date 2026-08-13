// 部门管理接口
import request from './request'

/** 查询部门列表（不分页） GET /depts */
export function listDept() {
  return request.get('/depts')
}

/** 根据 ID 查询部门 GET /depts/{id} */
export function getDept(id) {
  return request.get(`/depts/${id}`)
}

/** 新增部门 POST /depts { name } */
export function addDept(data) {
  return request.post('/depts', data)
}

/** 修改部门 PUT /depts { id, name } */
export function updateDept(data) {
  return request.put('/depts', data)
}

/** 删除部门 DELETE /depts/{id} */
export function deleteDept(id) {
  return request.delete(`/depts/${id}`)
}
