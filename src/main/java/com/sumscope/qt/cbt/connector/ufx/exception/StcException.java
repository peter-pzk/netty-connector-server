package com.sumscope.qt.cbt.connector.ufx.exception;


/**
 * STC 自定义异常。
 *
 * @date 2020-06-15
 */
public class StcException extends RuntimeException {

    /**
     * 错误信息。
     */
    private final String errorMsg;

    public StcException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
