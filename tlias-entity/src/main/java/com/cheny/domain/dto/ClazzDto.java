package com.cheny.domain.dto;


import com.cheny.domain.enums.SubjectEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ClazzDto {

    private Integer id;

    private String name;
    @NotBlank
    private String room;
    @NotNull
    private Date beginDate;
    @NotNull
    private Date endDate;

    private Integer masterId;

    private SubjectEnum subject;

}
