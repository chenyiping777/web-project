package com.cheny.exception;

import com.cheny.domain.entity.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @Valid + @RequestBody 的 JSON 请求体校验失败
     * （最常用：如 EmpDto 上的 @NotBlank 非空、@Pattern 手机号格式等）
     * 取出第一条字段校验错误的 message 返回给前端
     */
    @ExceptionHandler
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败：{}", message);
        return Result.error(message);
    }

    /**
     * @Validated 单个参数校验失败
     * （Controller 方法参数上直接加 @NotBlank/@Pattern 等注解的场景）
     */
    @ExceptionHandler
    public Result handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("参数校验失败");
        log.warn("参数校验失败：{}", message);
        return Result.error(message);
    }

    /**
     * 表单/查询参数对象绑定校验失败
     * （@Valid 加在非 @RequestBody 的对象参数上时抛 BindException）
     * 注意：MethodArgumentNotValidException 是 BindException 的子类，
     * Spring 会优先匹配上面更具体的那个方法，这里兜住其余的绑定校验
     */
    @ExceptionHandler
    public Result handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数绑定校验失败：{}", message);
        return Result.error(message);
    }

    /**
     * 请求体解析失败（JSON 格式非法、枚举值不在范围内等）
     */
    @ExceptionHandler
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败", e);
        return Result.error("请求参数格式不正确");
    }

    /**
     * 缺少必传的查询参数（如分页接口没传 pageNo/pageSize）
     */
    @ExceptionHandler
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return Result.error("缺少请求参数：" + e.getParameterName());
    }

    //唯一键冲突异常
    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        log.error("程序出错");
        String message = e.getMessage();
        int i = message.indexOf("Duplicate entry");//0
        String subStr = message.substring(i);
        String[] arr = subStr.split(" ");//1
        return Result.error(arr[2]);//2
    }

    //兜底：其他所有未处理的异常
    @ExceptionHandler
    public Result handleException(Exception e){
            log.error("程序出错",e);//在日志里查看具体的出错信息
            return Result.error("程序出错，赶快找后端");
    }
}
