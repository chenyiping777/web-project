package com.cheny.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
//是继承结构下实体类的标准配置，保证父类公共字段（主键、创建信息等）
//参与对象相等性判断，规避 Lombok 默认忽略父类字段带来的业务逻辑 BUG。
public class EmpQuery extends PageQuery {
    //用户查询条件实体   具体满足的可分页的信息
    private Integer gender;
    private String deptName;
}
