package com.cheny.domain.dto;


import com.cheny.domain.entity.EmpExpr;
import com.cheny.domain.enums.GenderEnum;
import com.cheny.domain.enums.JobEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmpDto {

    private Integer id;
    @NotBlank
    private String username;
    @NotBlank
    private String name;
    @NotNull
    private GenderEnum gender;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    private String image;
    private Integer deptId;
    private Date entryDate;
    private JobEnum job;
    private Double salary;
    private List<EmpExpr> experList;
}
