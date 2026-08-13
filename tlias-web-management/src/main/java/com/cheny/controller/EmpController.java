package com.cheny.controller;


import com.cheny.anno.Log;
import com.cheny.domain.dto.EmpDto;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.EmpQuery;
import com.cheny.domain.vo.EmpVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.service.EmpService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emp")
public class EmpController {

    @Autowired
    private EmpService empService;

    @Log
    @PostMapping("/getAllEmp")
    public Result getAllEmp(@RequestBody EmpQuery empQuery){
        PageVo<EmpVo> empList = empService.getAllEmpPage(empQuery);
        return Result.success(empList);
    }
    @Log
    @PostMapping
    public Result addEmp(@RequestBody @Valid EmpDto empDto){
        log.info("新增员工,接收参数:{}", empDto);
        try {
            empService.addEmp(empDto);
            return Result.success();
        } catch (RuntimeException e) {
            log.error("新增员工失败", e);
            return Result.error(e.getMessage());
        }
    }
    @Log
    @PutMapping
    public Result updateEmp(@RequestBody @Valid EmpDto empDto){
        log.info("修改员工,接收参数:{}", empDto);
        try {
            empService.updateEmp(empDto);
            return Result.success();
        } catch (RuntimeException e) {
            log.error("修改员工失败", e);
            return Result.error(e.getMessage());
        }
    }
    @Log
    @DeleteMapping("/{ids}")
    public Result deleteEmp(@PathVariable List<Integer> ids){
        log.info("删除员工,id:{}", ids);
        try {
            empService.deleteEmp(ids);
            return Result.success();
        } catch (RuntimeException e) {
            log.error("删除员工失败", e);
            return Result.error(e.getMessage());
        }
    }
    @Log
    @GetMapping("/{id}")
    public Result getEmpDetail(@PathVariable Integer id){
        log.info("查询员工详情,id:{}", id);
        try {
            EmpVo empVo = empService.getEmpDetail(id);
            return Result.success(empVo);
        } catch (RuntimeException e) {
            log.error("查询员工详情失败", e);
            return Result.error(e.getMessage());
        }
    }

}
