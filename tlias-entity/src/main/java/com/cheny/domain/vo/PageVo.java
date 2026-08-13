package com.cheny.domain.vo;

import lombok.Data;

import java.util.List;

//分页结果
@Data
public class PageVo<T> {
    private Integer total;//总条数
    private Integer pages;//总页数
    private List<T> list;

}
