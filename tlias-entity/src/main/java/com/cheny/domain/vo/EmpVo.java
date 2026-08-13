package com.cheny.domain.vo;
import com.cheny.domain.entity.EmpExpr;
import com.cheny.domain.enums.GenderEnum;
import com.cheny.domain.enums.JobEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmpVo {

    //修改场景，修改 用户名，姓名，性别，手机号，职位，薪资，所属部门，入职日期，头像，工作经历
    /**
     * 主键(修改时传入)
     */
    private Integer id;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码(新增员工时传入,为空则使用默认密码)
     */
    private String password;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotNull(message = "性别不能为空")
    private GenderEnum gender;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    /**
     * 职位(数据库 tinyint 编码,对应 JobEnum 中的枚举)
     */
    private JobEnum job;
    private Integer salary;
    private String image;
    /**
     * 部门名称(仅查询展示用)
     */
    private String deptName;
    //        private Integer deptId;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private List<EmpExpr> exprList;

}
