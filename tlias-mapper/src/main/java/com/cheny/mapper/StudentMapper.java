package com.cheny.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheny.domain.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheny.domain.vo.StudentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Mlpnk
* @description 针对表【student(学员表)】的数据库操作Mapper
* @createDate 2026-08-08 15:15:22
* @Entity com.cheny.domain.Student
*/
public interface StudentMapper extends BaseMapper<Student> {
    // 用通用 Wrapper 接收，允许传带表别名的 QueryWrapper（避免联表 name 列歧义）
    Page<StudentVo> selectStudentPage(Page<StudentVo> page,@Param("ew") Wrapper<Student> wrapper);
    /** 直接查班级名称+对应人数，排序 */
    List<Map<String,Object>> countByClazz();
    List<Map<String,Object>> countByDegree();
}




