package com.cheny.service;

import com.cheny.domain.dto.StudentDto;
import com.cheny.domain.entity.Result;
import com.cheny.domain.entity.Student;
import com.baomidou.mybatisplus.spring.service.IService;
import com.cheny.domain.query.StudentQuery;
import com.cheny.domain.vo.PageVo;
import com.cheny.domain.vo.StudentVo;
import jakarta.validation.Valid;

import java.util.List;

/**
* @author Mlpnk
* @description 针对表【student(学员表)】的数据库操作Service
* @createDate 2026-08-08 15:15:22
*/
public interface StudentService extends IService<Student> {

    PageVo<StudentVo> getStudentPage(StudentQuery studentQuery);

    Result deleteStudents(List<Integer> ids);

    Result addStudent(@Valid StudentDto studentDto);
    
    Result getStudentById(@Valid Integer id);

    Result updateStudentById(StudentDto studentDto);
}
