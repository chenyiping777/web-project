package com.cheny.domain.vo;

import com.cheny.domain.enums.DegreeEnum;
import com.cheny.domain.enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
public class StudentVo {

    //响应的数据

    private Integer id;

    private String name;

    private String no;//学号

    private GenderEnum gender;

    private String phone;
    private String idCard;
    private Boolean isCollege;
    private String address;

    private DegreeEnum degree;
    @JsonFormat(pattern = "yyyy‑MM‑dd", timezone = "GMT+8")
    private Date graduationDate;

    private Integer clazzId;

    private Integer violationCount;// 违纪次数

    private Integer violationScore;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy‑MM‑dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
