package com.cheny.domain.dto;

import com.cheny.domain.enums.DegreeEnum;
import com.cheny.domain.enums.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class StudentDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String no;//学号
    @NotNull
    private GenderEnum gender;
    @NotBlank
    private String phone;
    private String idCard;
    private Boolean isCollege;
    private String address;
    @NotNull
    private DegreeEnum degree;
    private Date graduationDate;
    @NotNull(message = "班级ID不能为空")
    private Integer clazzId;
    private Integer violationCount;// 违纪次数
    private Integer violationScore;
    private Date createTime;
    private Date updateTime;
}
