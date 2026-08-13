package com.cheny.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.cheny.domain.entity.OperateLog;
import com.cheny.domain.query.PageQuery;
import com.cheny.domain.vo.OperateLogVo;
import com.cheny.domain.vo.PageVo;

public interface OperateLogService extends IService<OperateLog> {

    /** 操作日志分页查询（按操作时间倒序） */
    PageVo<OperateLogVo> pageQuery(PageQuery pageQuery);
}
