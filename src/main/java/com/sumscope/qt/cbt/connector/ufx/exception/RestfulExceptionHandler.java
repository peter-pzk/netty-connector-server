package com.sumscope.qt.cbt.connector.ufx.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * Restful 异常处理器。
 *
 * @date 2020-08-14
 */
public class RestfulExceptionHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        int status = statusCode.value();
        if (status == 400 || status == 500 || status == 404) {
            throw new StcException("[RESTful request failed] - Code: " +
                    statusCode.value() + ", Error: " + response.getStatusText());
        } else {
            super.handleError(response, statusCode);
        }
    }

}
