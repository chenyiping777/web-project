// 班级管理接口
import request from './request'

/**
 * 分页查询班级列表
 * GET /clazz，查询参数 ClazzQuery：{ pageNo, pageSize, subject, beginDate, endDate }
 */
export function pageClazz(params) {
  return request.get('/clazz', { params })
}

/** 查询所有班级（不分页，下拉选用） GET /clazz/all */
export function listAllClazz() {
  return request.get('/clazz/all')
}

/** 根据 ID 查询班级 GET /clazz/{id} */
export function getClazz(id) {
  return request.get(`/clazz/${id}`)
}

/** 新增班级 POST /clazz（ClazzDto） */
export function addClazz(data) {
  return request.post('/clazz', data)
}

/** 修改班级 PUT /clazz（ClazzDto，必须带 id 和 masterId） */
export function updateClazz(data) {
  return request.put('/clazz', data)
}

/** 删除班级 DELETE /clazz/{id} */
export function deleteClazz(id) {
  return request.delete(`/clazz/${id}`)
}
