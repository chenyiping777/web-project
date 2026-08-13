package com.cheny.service;

import com.cheny.domain.dto.ClazzDto;
import com.cheny.domain.entity.Clazz;
import com.baomidou.mybatisplus.spring.service.IService;
import com.cheny.domain.entity.Result;
import com.cheny.domain.query.ClazzQuery;
import com.cheny.domain.vo.ClazzVo;
import com.cheny.domain.vo.EmpVo;
import com.cheny.domain.vo.PageVo;

/**
* @author Mlpnk
* @description 针对表【clazz(班级表)】的数据库操作Service
* @createDate 2026-08-08 15:15:16
*/
public interface ClazzService extends IService<Clazz> {
    PageVo<ClazzVo> getClazz(ClazzQuery clazzQuery);

    Result deleteClazz(Integer id);

    Result addClazz(ClazzDto clazzDto);

    Result getClazzOne(Integer id);

    Result updateClazz(ClazzDto clazzDto);

    Result getAllClazz();
}
