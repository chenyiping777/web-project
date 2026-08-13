package com.cheny.controller;


import com.cheny.anno.Log;
import com.cheny.domain.entity.Dept;
import com.cheny.domain.entity.Result;
import com.cheny.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;
    @GetMapping
    public Result getDept(){
        /*
        * 1.部门数据较少，不考虑分页展示
        * 2.对查询的结果，根据最后修改时间倒序排序
        * */
        log.info("展示部门");

        List<Dept> deptList = deptService.listAllOrderByUpdateTime();

        return Result.success(deptList);
    }
    @Log
    @DeleteMapping("{id}")
    public Result moveDept(@PathVariable Integer id){
        log.info("待删除部门：{}",id);
        deptService.removeById(id);
        return Result.success();
    }

    @Log
    @PostMapping
    public Result saveDept(@RequestBody Dept dept){
//        新增部门：部门名称必填，唯一，长度为2-10位
        log.info("新增部门，接收参数:{}",dept);
        log.info("待新增部门名称：{}",dept.getName());
      try{
          deptService.saveDept(dept);
          return Result.success();
      } catch (RuntimeException e) {
          log.error("新增部门失败",e);
          return Result.error();
      }
    }

    @Log
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("接收查询部门请求 ，id = {}",id);
        Dept dept = deptService.getById(id);//(@PathVariable("id") Integer id
        log.info("待修改部门：{}",dept);
        return Result.success(dept);
    }


    @Log
    @PutMapping
    public Result updateDept(@RequestBody Dept dept){//后面的deptId和路径里的id名字不一样，才需要在括号里面标注“id”
        //dept包括：id name
        deptService.updateDeptById(dept);//(@PathVariable("id") Integer deptId
        log.info("修改后的部门：{}",dept);
        return Result.success(dept);
    }
}
