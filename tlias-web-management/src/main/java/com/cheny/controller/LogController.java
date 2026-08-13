package com.cheny.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheny.anno.Log;
import com.cheny.domain.entity.OperateLog;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.PageQuery;
import com.cheny.domain.vo.OperateLogVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.service.OperateLogService;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private OperateLogService operateLogService;

    // 5.5 日志信息分页查询
    @Log
    @GetMapping("/page")
    public Result page(PageQuery page) {

        log.info("操作日志分页查询: page={}, pageSize={}", page.getPageNo(), page.getPageSize());

        PageVo<OperateLogVo> data = operateLogService.pageQuery(page);
        return Result.success(data);
    }
}
