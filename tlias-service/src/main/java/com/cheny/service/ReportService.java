package com.cheny.service;

import java.util.List;
import java.util.Map;


//因为这个没有实体类，所以不继承IService
public interface ReportService {

    /** 员工性别统计 */
    List<Map<String, Object>> empGenderData();

    /** 员工职位人数统计 */
    List<Map<String, Object>> empJobData();

    /** 学员学历统计 */
    List<Map<String, Object>> studentDegreeData();

    /** 班级人数统计 */
    Map<String, Object> studentCountData();
}
