package com.cheny.controller;


import com.cheny.anno.Log;
import com.cheny.domain.dto.ClazzDto;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.ClazzQuery;
import com.cheny.domain.vo.ClazzVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.service.ClazzService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/clazz")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

//    url 问号后面的参数：简单参数 @RequestParam，封装对象就什么注解都不写；
//    JSON 请求体才用 @RequestBody；路径大括号变量用 @PathVariable。
    @Log
    @GetMapping
    public Result getClazz(ClazzQuery clazzQuery){
        PageVo<ClazzVo> empList = clazzService.getClazz(clazzQuery);
        if(empList!=null)   return Result.success(empList);
        else return Result.error();
    }

    //删除班级
    @Log
    @DeleteMapping("/{id}")
    public Result deleteClazz(@PathVariable Integer id){
        return clazzService.deleteClazz(id);
    }

    //添加班级
    @Log
    @PostMapping
    public Result addClazz(@RequestBody @Valid ClazzDto clazzDto){
        return clazzService.addClazz(clazzDto);
    }

    //查询班级
    @Log
    @GetMapping("/{id}")
    public Result getClazzOne(@PathVariable Integer id){
        return clazzService.getClazzOne(id);
    }

    //修改班级
    @Log
    @PutMapping
    public Result updateClazz(@RequestBody ClazzDto clazzDto){
        return clazzService.updateClazz(clazzDto);
    }

    //查询所有班级
    @Log
    @GetMapping("/all")
    public Result getAllClazz(){
        return clazzService.getAllClazz();
    }

}
