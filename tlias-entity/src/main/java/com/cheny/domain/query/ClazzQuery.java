package com.cheny.domain.query;

import com.cheny.domain.enums.SubjectEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClazzQuery extends PageQuery {
    private SubjectEnum subject;
    // 查询参数是字符串（如 ?beginDate=2023-01-01），需告诉 Spring 按 yyyy-MM-dd 解析成 Date
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;//在这一段时间内结课的


}
