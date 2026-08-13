package com.cheny.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cheny.domain.entity.EmpExpr;
import com.cheny.mapper.EmpExprMapper;
import com.cheny.service.EmpExprService;
import org.springframework.stereotype.Service;

/**
* @author Mlpnk
* @description 针对表【emp_expr(工作经历表)】的数据库操作Service实现
* 工作经历是 emp 的子表,不单独提供 Controller,所有操作由 EmpService 内部调用
*/
@Service
public class EmpExprServiceImpl extends ServiceImpl<EmpExprMapper, EmpExpr>
    implements EmpExprService{

}
