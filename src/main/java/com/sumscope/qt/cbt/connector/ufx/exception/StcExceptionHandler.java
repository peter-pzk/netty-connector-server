package com.sumscope.qt.cbt.connector.ufx.exception;

import com.sumscope.qt.cbt.connector.ufx.util.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.invoke.MethodHandles;
import java.nio.file.AccessDeniedException;

/**
 * STC 异常处理器。
 *
 * @date 2020-06-15
 */
@RestControllerAdvice
public class StcExceptionHandler {

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * STC 异常处理方法。
     *
     * @param stcException STC 异常。
     * @return 异常响应结果。
     */
    @ExceptionHandler(value = StcException.class)
    public ResponseResult<String> stcExceptionHandler(StcException stcException) {
        return ResponseResult.error(stcException.getMessage());
    }

    /**
     * 授权接口异常处理方法。
     *
     * @param exception 不允许访问异常。
     * @return 异常响应结果。
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseResult<String> accessDeniedExceptionHandler(AccessDeniedException exception) {
        LOGGER.error("Disallow access.");
        return ResponseResult.error("该用户不允许访问此接口");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult<String> validationExceptionHandler(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        LOGGER.error("Controller validation exception. Validation message: {}", message);
        return ResponseResult.error(message);
    }

    /**
     * 未定义异常处理方法。
     *
     * @param exception 未定义异常。
     * @return 异常响应结果。
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseResult<String> exceptionHandler(Exception exception) {
        LOGGER.error("STC system internal unknown exception", exception);
        return ResponseResult.error("系统内部异常");
    }
}
