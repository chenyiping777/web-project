package com.cheny.service;

import com.cheny.domain.entity.Dept;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
* @author Mlpnk
* @description 针对表【dept(部门表)】的数据库操作Service
* @createDate 2026-08-05 22:11:50
*/
public interface DeptService extends IService<Dept> {
     List<Dept> listAllOrderByUpdateTime();

    void saveDept(Dept dept);
    void updateDeptById(Dept dept);
}
