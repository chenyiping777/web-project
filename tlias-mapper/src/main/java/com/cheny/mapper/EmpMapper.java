package com.cheny.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cheny.domain.entity.Emp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheny.domain.vo.EmpVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author Mlpnk
* @description 针对表【emp(员工表)】的数据库操作Mapper
* @createDate 2026-08-06 14:13:13
* @Entity com.cheny.domain.Emp
*/
public interface EmpMapper extends BaseMapper<Emp> {
    IPage<EmpVo> selectEmpVoPage(IPage<EmpVo> page, @Param("ew") QueryWrapper<?> wrapper);
    List<Map<String,Object>> countByEmp();
}




