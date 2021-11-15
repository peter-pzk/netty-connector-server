package com.sumscope.qt.cbt.connector.ufx.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Response result view class.
 *
 * @date 2020-05-19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "视图响应结果")
public class ResponseResult<T> {

    /**
     * Object when status is SUCCESS.
     */
    @ApiModelProperty(value = "内容")
    private T content;
    /**
     * When status is FAIL.
     */
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;
    /**
     * Request status.
     */
    @ApiModelProperty(value = "响应状态")
    private ResponseStatus status;

    public ResponseResult() {

    }

    public ResponseResult(T content, String errorMessage, ResponseStatus status) {
        this.content = content;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    /**
     * Response result when status is FAIL.
     *
     * @return response result.
     */
    public static ResponseResult<String> error(String errorMessage) {
        return new ResponseResult<>(null, errorMessage, ResponseStatus.FAIL);
    }

    /**
     * Response result when status is SUCCESS.
     *
     * @param obj return content.
     * @return response result.
     */
    public static <T> ResponseResult<T> ok(T obj) {
        return new ResponseResult<>(obj, null, ResponseStatus.SUCCESS);
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public enum ResponseStatus {
        /**
         * SUCCESS.
         */
        SUCCESS,

        /**
         * FAIL.
         */
        FAIL
    }
}
