package com.chenyuxin.rear.common.exception;

import com.chenyuxin.rear.common.model.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex, HttpServletRequest request) {
        log.error("异常信息描述:",ex);
        return Result.failure(500, "服务器异常");
    }

    /**
     * 处理业务异常（自定义的 BusinessException）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        log.error("异常信息描述:",ex);
        return Result.failure(ex.getCode(), ex.getMessage());
    }

    /**
     * 统一处理所有参数校验相关的异常
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })

 public Result<Void> handleValidationExceptions(Exception ex) {
        String message = "参数异常";
        if (ex instanceof MethodArgumentNotValidException e) {
            message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        } else if (ex instanceof BindException e) {
            message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        } else if (ex instanceof ConstraintViolationException e) {
            message = e.getConstraintViolations().iterator().next().getMessage();
        } else if (ex instanceof MissingServletRequestParameterException e) {
            message = "缺少参数: " + e.getParameterName();
        } else if (ex instanceof MethodArgumentTypeMismatchException e) {
            message = "参数类型错误: " + e.getName();
        } else if (ex instanceof HttpMessageNotReadableException) {
            message = "请求体格式错误";
        }
        log.error("异常信息描述:",ex);
        return Result.failure(400, message);
    }


}
