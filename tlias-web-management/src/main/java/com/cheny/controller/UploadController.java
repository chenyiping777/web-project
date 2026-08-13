package com.cheny.controller;

import com.cheny.anno.Log;
import com.cheny.domain.entity.Result;
import com.cheny.utils.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* 文件上传接口：前端把文件作为 multipart/form-data 提交，这里转交 AliOSSUtils 传到阿里云 OSS
*/
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    /**
    * @param file MultipartFile 就是 Spring 封装好的"上传文件"对象，
    *             声明规则：参数名要和前端表单里 <input type="file" name="file"> 的 name 一致
    */
    @Log
    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        //前端通过form-data表单上传文件时，Spring 框架自动把上传的二进制文件、文件名、
        // 文件类型、文件大小全部封装进MultipartFile对象，后端直接接收使用，
        // 不用自己解析原生 IO 流。
        // 1. 上传到 OSS，返回图片访问地址
        String url = aliOSSUtils.upload(file);

        // 2. 把地址装进 Result 返回给前端
        return Result.success(url);
    }
}
