package com.cheny.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cheny.domain.entity.OperateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cheny.domain.vo.OperateLogVo;


/**
* @author Mlpnk
* @description 针对表【operate_log(操作日志表)】的数据库操作Mapper
* @createDate 2026-08-09 14:22:57
* @Entity com.cheny.domain.OperateLog
*/
public interface OperateLogMapper extends BaseMapper<OperateLog> {
    IPage<OperateLogVo> selectOperateVoPage(Page<OperateLogVo> page);
}




