package com.sumscope.qt.cbt.connector.ufx.util;

/**
 * @author peter-pan
 */

public enum UFXFuncNoConstant {

    /**
     * 银行间债券双边报价做市委托查询
     */
    BANK_TWO_WAY_QRY(91137, "银行间债券双边报价做市委托查询"),

    /**
     * 银行间债券双边报价做市委托
     */
    BANK_TWO_WAY_QUOTE_ENTRUST(91138, "银行间债券双边报价做市委托"),

    /**
     * 银行间债券双边报价做市委托撤单
     */
    BANK_TWO_WAY_QUOTE_WITHDRAW(91139, "银行间债券双边报价做市委托撤单"),

    /**
     * 银行间做市点击成交报价委托查询
     */
    BANK_BOND_ANONYMOUS_QUOTE_QRY(91143, "银行间做市点击成交报价委托查询"),

    /**
     * 银行间做市请求回复委托
     */
    BANK_MTK_REQUEST_FUNC_NO(91144, "银行间做市请求回复委托"),

    /**
     * 银行间做市请求回复拒绝
     */
    BANK_MTK_REQUEST_REFUSE_FUNC_NO(91145, "银行间做市请求回复拒绝"),

    /**
     * 银行间做市请求回复撤销
     */
    BANK_MTK_REQUEST_CANCEL_FUNC_NO(91146, "银行间做市请求回复撤销"),

    /**
     * 银行间做市请求回复委托流水查询
     */
    BANK_MTK_REQUEST_QRY_FUNC_NO(91147, "银行间做市请求回复委托流水查询");

    private final Integer funcNo;
    private final String name;

    UFXFuncNoConstant(Integer funcNo, String name) {
        this.funcNo = funcNo;
        this.name = name;
    }

    public Integer getFuncNo() {
        return funcNo;
    }

    public String getName() {
        return name;
    }
}
