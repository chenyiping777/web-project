package com.cheny.service;

import com.cheny.domain.dto.EmpLoginDto;
import com.cheny.domain.entity.Emp;
import com.baomidou.mybatisplus.spring.service.IService;
import com.cheny.domain.dto.EmpDto;
import com.cheny.domain.query.EmpQuery;
import com.cheny.domain.vo.EmpVo;
import com.cheny.domain.vo.PageVo;

import java.util.List;

/**
* @author Mlpnk
* @description 针对表【emp(员工表)】的数据库操作Service
* @createDate 2026-08-06 14:13:13
*/

public interface EmpService extends IService<Emp> {

    PageVo<EmpVo> getAllEmpPage(EmpQuery empQuery);

    /**
     * 新增员工,前端传 EmpVo(含工作经历 exprList)
     */
    void addEmp(EmpDto empDto);

    /**
     * 修改员工,前端传 EmpVo(含 id 和工作经历 exprList,经历采用删旧插新)
     */
    void updateEmp(EmpDto empDto);

    /**
     * 删除员工,先删工作经历再删员工
     */
    void deleteEmp(List<Integer> ids);

    /**
     * 员工详情(编辑回显用),含部门名称和工作经历
     */
    EmpVo getEmpDetail(Integer id);


    /**
     * 员工登录
     */

    Integer login(EmpLoginDto empLoginDto);
}
