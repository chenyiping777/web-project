package com.cheny.controller;

import com.cheny.anno.Log;
import com.cheny.domain.dto.EmpLoginDto;
import com.cheny.domain.entity.Result;
import com.cheny.service.EmpService;
import com.cheny.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmpService empService;


    @Log
    @PostMapping("/login")
    public Result login(@Valid @RequestBody EmpLoginDto empLoginDto) {

        log.info("用户登录，用户名：{}", empLoginDto);

        int userId = empService.login(empLoginDto);
        if (userId>0) {
            String token = jwtUtil.createToken(userId);
            return Result.success(token);
        }
        return Result.error("用户名或密码错误");
    }
}
