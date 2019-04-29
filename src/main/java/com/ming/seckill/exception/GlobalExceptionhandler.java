package com.ming.seckill.exception;

import com.ming.seckill.controller.GoodsController;
import com.ming.seckill.result.CodeMsg;
import com.ming.seckill.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionhandler {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);


    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        logger.error(e.getMessage());
        if (e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return Result.error(exception.getCodeMsg());
        }
        else if (e instanceof BindException){
            BindException exception = (BindException) e;
            List<ObjectError> errors = exception.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
