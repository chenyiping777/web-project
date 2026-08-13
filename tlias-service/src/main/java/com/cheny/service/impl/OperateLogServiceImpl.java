package com.cheny.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;

import com.cheny.domain.entity.OperateLog;
import com.cheny.domain.query.PageQuery;

import com.cheny.domain.vo.OperateLogVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.mapper.EmpMapper;
import com.cheny.mapper.OperateLogMapper;
import com.cheny.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog>
        implements OperateLogService {

    @Autowired
    private EmpMapper empMapper;

    @Override
    public PageVo<OperateLogVo> pageQuery(PageQuery pageQuery) {
        int pageNo = pageQuery.getPageNo();
        int pageSize = pageQuery.getPageSize();

        //没有查询条件，只是单纯的分页查询
        Page<OperateLogVo> page = new Page<>(pageNo, pageSize);
        IPage<OperateLogVo> pageResult = baseMapper.selectOperateVoPage(page);

        //封装分页查询结果
        PageVo<OperateLogVo> vo = new PageVo<>();
        vo.setTotal((int) pageResult.getTotal());
        vo.setPages((int) pageResult.getPages());
        vo.setList(pageResult.getRecords());
        return vo;

    }

}
