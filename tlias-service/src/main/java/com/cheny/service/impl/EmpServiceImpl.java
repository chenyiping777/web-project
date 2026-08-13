package com.cheny.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cheny.domain.dto.EmpDto;
import com.cheny.domain.dto.EmpLoginDto;

import com.cheny.domain.entity.Dept;
import com.cheny.domain.entity.Emp;
import com.cheny.domain.entity.EmpExpr;

import com.cheny.domain.query.EmpQuery;
import com.cheny.domain.vo.EmpVo;
import com.cheny.domain.vo.PageVo;
import com.cheny.mapper.EmpMapper;
import com.cheny.service.DeptService;
import com.cheny.service.EmpExprService;
import com.cheny.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @author Mlpnk
* @description 针对表【emp(员工表)】的数据库操作Service实现
* 说明:emp 和 emp_expr 是主从表关系,emp_expr 没有独立的 Controller,
*       所有对子表的操作(新增/回显/修改/删除)都在本 Service 内部完成,
*       通过注入 EmpExprService 实现 —— Service 层可以操作任意多张表,不限于一个 Mapper。
*/
@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp>
    implements EmpService{

    /** 工作经历子表 Service */
    @Autowired
    private EmpExprService empExprService;

    /** 部门 Service(详情回显部门名称用) */
    @Autowired
    private DeptService deptService;

    @Override
    public PageVo<EmpVo> getAllEmpPage(EmpQuery empQuery) {

        // 直接取值，无需判空，实体自带默认值
        Page<EmpVo> pageParam = new Page<>(empQuery.getPageNo(), empQuery.getPageSize());

        // 排序逻辑不变
        String sortBy = empQuery.getSortBy();
        boolean asc = empQuery.getAsc()!=null&&empQuery.getAsc();
        if (StringUtils.hasText(sortBy)) {
            pageParam.addOrder(asc ? OrderItem.asc(sortBy) : OrderItem.desc(sortBy));
        } else {
            //默认排序
            pageParam.addOrder(OrderItem.desc("salary"), OrderItem.asc("entry_date"));
        }

        // 条件链式构造
        QueryWrapper<?> wrapper = new QueryWrapper<>()
                .eq(empQuery.getGender() != null, "e.gender", empQuery.getGender())
                .like(StringUtils.hasText(empQuery.getDeptName()), "d.name", empQuery.getDeptName());

        IPage<EmpVo> pageResult = baseMapper.selectEmpVoPage(pageParam, wrapper);

        // 封装返回
        PageVo<EmpVo> vo = new PageVo<>();
        vo.setTotal((int) pageResult.getTotal());
        vo.setPages((int) pageResult.getPages());
        vo.setList(pageResult.getRecords());
        return vo;

    }


    @Override //  接口实现 / 父类重写方法
    @Transactional // 主表 + 子表同事务,任何一步失败整体回滚
    public void addEmp(EmpDto empDto) {
        // 必填字段校验(emp 表对应列为 NOT NULL) epmVo里面用注解解决了


        // 用户名唯一校验
        Long count = lambdaQuery().eq(Emp::getUsername, empDto.getUsername())
                                  .count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // EmpVo -> Emp,忽略 null 字段
        Emp emp = new Emp();
        BeanUtil.copyProperties(empDto, emp);

        // 密码为空时给默认密码
        if (!StringUtils.hasText(emp.getPassword())) {
            emp.setPassword("123456");
        }
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        emp.setEntryDate(LocalDateTime.now());

        // 1. 先插主表,save() 会自动把数据库生成的主键回填到 emp.getId()
        save(emp);

        // 2. 再插子表:给每条工作经历补上 emp_id,然后批量插入
        List<EmpExpr> exprList = empDto.getExperList();
        //工作经历如果为空就不添加
        if (CollUtil.isNotEmpty(exprList)) {
            for (EmpExpr e : exprList) {
                e.setEmpId(emp.getId()); // 关联主表主键
            }
            empExprService.saveBatch(exprList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //修改场景，修改 用户名，姓名，性别，手机号，职位，薪资，所属部门，入职日期，头像，工作经历
    //             前四个不能为空
    public void updateEmp(EmpDto empDto) {

        if (empDto.getId() == null) {
            throw new RuntimeException("员工ID不能为空");
        }
        // 用户名唯一校验(排除自己)
        Long count = lambdaQuery().eq(Emp::getUsername, empDto.getUsername())
                .ne(Emp::getId, empDto.getId()).count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 主表:只更新非 null 字段,密码不传则保持原样
        Emp emp = new Emp();
        BeanUtil.copyProperties(empDto, emp);

        emp.setUpdateTime(LocalDateTime.now());

//        updateById(emp) 执行的是动态 SQL，规则由全局字段更新策略 field-strategy 控制，默认值为 NOT_NULL：
//        实体类中值为 null 的属性，不会拼入 UPDATE 语句的 SET 子句，数据库对应字段保持原值不变；
//        只有非 null 的字段才会出现在 SET 里，被更新为新值。

        updateById(emp);

        // 子表策略:删旧插新 —— 把旧的经历全部删掉,再按前端提交的重新插入,不用逐条比对
        empExprService.remove(new QueryWrapper<EmpExpr>().eq("emp_id", empDto.getId()));
        List<EmpExpr> exprList = empDto.getExperList();
        if (CollUtil.isNotEmpty(exprList)) {
            for (EmpExpr e : exprList) {
                e.setId(null);
                e.setEmpId(empDto.getId());
            }
            empExprService.saveBatch(exprList);
        }
    }

    @Override
    @Transactional
    public void deleteEmp(List<Integer> ids) {
        // 先删子表(工作经历),再删主表 —— 避免残留孤儿数据

        empExprService.lambdaUpdate().in(EmpExpr::getEmpId,ids).remove();
        removeBatchByIds(ids);
    }

    @Override
    public EmpVo getEmpDetail(Integer id) {
        Emp emp = getById(id);
        if (emp == null) {
            throw new RuntimeException("员工不存在");
        }
        EmpVo vo = new EmpVo();
        BeanUtil.copyProperties(emp, vo);
        vo.setPassword(null); // 不回传密码

        // 部门名称
        if (emp.getDeptId() != null) {
            Dept dept = deptService.getById(emp.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getName());
            }
        }

        // 工作经历:按 emp_id 查子表,按开始时间排序,方便前端回显
        List<EmpExpr> exprList = empExprService.lambdaQuery()
                .eq(EmpExpr::getEmpId, id)
                .orderByAsc(EmpExpr::getBegin)
                .list();
        vo.setExprList(exprList);
        return vo;
    }

    @Override
    public Integer login(EmpLoginDto empLoginDto) {
        if (empLoginDto == null)
            throw new RuntimeException("用户名和密码不能为空");
        //校验用户名和密码，返回userId
        String username = empLoginDto.getUsername();
        String password = empLoginDto.getPassword();
        Emp emp = lambdaQuery().eq(Emp::getUsername, username)
                .eq(Emp::getPassword, password)
                .one();
        if (emp == null) {
            return -1;
        }
        return emp.getId();
    }


}
