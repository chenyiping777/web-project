package com.cheny.controller;

import com.cheny.anno.Log;
import com.cheny.domain.entity.Result;
import com.cheny.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 5.1 统计员工性别信息
    @Log
    @GetMapping("/empGenderData")
    public Result empGenderData() {
        log.info("查询员工性别统计");
        return Result.success(reportService.empGenderData());
    }

    // 5.2 统计员工职位人数
    @Log
    @GetMapping("/empJobData")
    public Result empJobData() {
        log.info("查询员工职位统计");
        return Result.success(reportService.empJobData());
    }

    // 5.3 统计学员学历
    @Log
    @GetMapping("/studentDegreeData")
    public Result studentDegreeData() {
        log.info("查询学员学历统计");
        return Result.success(reportService.studentDegreeData());
    }

    // 5.4 统计班级人数
    @Log
    @GetMapping("/studentCountData")
    public Result studentCountData() {
        log.info("查询班级人数统计");
        return Result.success(reportService.studentCountData());
    }
}
