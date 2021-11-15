package com.sumscope.qt.cbt.connector.ufx.util;

/**
 * Customize Constants.
 *
 * @date 2020-05-18
 */
public class CustomConstants {

    /**
     * Map initialize capacity.
     */
    public static final int MAP_INITIALIZE_CAPACITY = 1 << 4;


    /**
     * Token 通用前缀。
     */
    public static final String TOKEN_GENERIC_PREFIX = "Bearer ";

    /**
     * 返回值，多用于修改、新增、删除请求。
     */
    public static final String _OK = "OK";

    public static final String REST_START_LOG_TEMPLATE = "[RESTful request] - url: %s, method: %s, args: %s";
    public static final String REST_END_LOG_TEMPLATE = "[RESTful request] - url: %s, method: %s, return: %s";
}
