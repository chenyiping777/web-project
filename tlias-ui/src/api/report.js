// 数据统计 + 操作日志接口
import request from './request'

/** 员工性别统计 GET /report/empGenderData -> [{name,value}] */
export function empGenderData() {
  return request.get('/report/empGenderData')
}

/** 员工职位统计 GET /report/empJobData -> [{job,value}] */
export function empJobData() {
  return request.get('/report/empJobData')
}

/** 学员学历统计 GET /report/studentDegreeData -> [{degree,value}] */
export function studentDegreeData() {
  return request.get('/report/studentDegreeData')
}

/** 班级人数统计 GET /report/studentCountData -> {clazzNameList,studentCountList} */
export function studentCountData() {
  return request.get('/report/studentCountData')
}

/**
 * 操作日志分页查询
 * GET /log/page，查询参数：{ pageNo, pageSize }
 */
export function pageLog(params) {
  return request.get('/log/page', { params })
}
