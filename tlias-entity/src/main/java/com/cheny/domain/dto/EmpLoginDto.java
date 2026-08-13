package com.cheny.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmpLoginDto {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}

