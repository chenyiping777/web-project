package com.cheny.controller;


import com.cheny.anno.Log;
import com.cheny.domain.dto.StudentDto;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.StudentQuery;
import com.cheny.domain.vo.PageVo;
import com.cheny.domain.vo.StudentVo;
import com.cheny.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {


    @Autowired
    private StudentService studentService;
    @GetMapping("/page")
    public Result getStudentPage(StudentQuery studentQuery) {
        log.info("接收分页查询学生请求，参数：{}", studentQuery);
        PageVo<StudentVo> vo =  studentService.getStudentPage(studentQuery);
        log.info("分页查询学生响应，结果：{}", vo);
        return Result.success(vo);
    }

    //删除班级
    @Log
    @DeleteMapping("/{ids}")
    public Result deleteStudents(@PathVariable List<Integer> ids){
        return studentService.deleteStudents(ids);
    }

    //添加班级
    @Log
    @PostMapping
    public Result addStudent(@RequestBody @Valid StudentDto studentDto){
        return studentService.addStudent(studentDto);
    }

    @GetMapping("/{id}")
    public Result getStudentById(@PathVariable @Valid Integer id){
        return studentService.getStudentById(id);
    }

    //修改班级
    @Log
    @PutMapping
    public Result updateStudent(@RequestBody StudentDto studentDto){
        return studentService.updateStudentById(studentDto);
    }


}
