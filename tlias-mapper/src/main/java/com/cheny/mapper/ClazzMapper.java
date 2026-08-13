package com.cheny.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cheny.domain.entity.Clazz;
import com.cheny.domain.vo.ClazzVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;


public interface ClazzMapper extends BaseMapper<Clazz> {
    IPage<ClazzVo> selectClazzVoPage(Page<ClazzVo> page, @Param("ew") QueryWrapper<Clazz> wrapper);
}




