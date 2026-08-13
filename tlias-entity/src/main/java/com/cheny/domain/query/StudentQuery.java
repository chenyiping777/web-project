package com.cheny.domain.query;


import com.cheny.domain.enums.DegreeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQuery extends PageQuery{
    private String name;
    private Integer clazzId;
    private DegreeEnum degree;
}
