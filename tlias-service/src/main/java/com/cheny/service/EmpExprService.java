package com.cheny.service;

import com.cheny.domain.entity.EmpExpr;
import com.baomidou.mybatisplus.spring.service.IService;

/**
* @author Mlpnk
* @description 针对表【emp_expr(工作经历表)】的数据库操作Service
* 工作经历是 emp 的子表,不单独提供 Controller,所有操作由 EmpService 内部调用
*/
public interface EmpExprService extends IService<EmpExpr> {

}
