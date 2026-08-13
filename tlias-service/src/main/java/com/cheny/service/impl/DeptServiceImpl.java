package com.cheny.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cheny.domain.entity.Dept;
import com.cheny.service.DeptService;
import com.cheny.mapper.DeptMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author Mlpnk
* @description 针对表【dept(部门表)】的数据库操作Service实现
* @createDate 2026-08-05 22:11:50
*/
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept>
    implements DeptService{

    @Override
    public List<Dept> listAllOrderByUpdateTime() {
        /*
        * 重写查询方法，要求按照updateTime进行降序排列
        * */
        //创建查询条件对象
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        //拼接where
        wrapper.orderByDesc(Dept::getUpdateTime);
        //查询
        return baseMapper.selectList(wrapper);//查多条
        //selectById 主键查单条  selectOne 唯一条件查单条
    }

    @Override
    public  void saveDept(Dept dept){
        //创建查询条件对象
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getName,dept.getName());
        Long count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new RuntimeException("该部门名称已经存在，不能重复添加");
        }

        //补全属性
        //校验通过，执行update

        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(dept);
    }

    @Override
    public  void updateDeptById(Dept dept){
        dept.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(dept);
    }
}




