package com.sumscope.qt.cbt.connector.ufx.controller.manager;

import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeResp;

/**
 * @author peter pan
 * @date 2021-11-09 17:31
 */
public class BankMtkRequestManager {
    public static SubscribeResp.BankMtkRequestResp toBankMtkRequestResp(IDatasets result) {
        SubscribeResp.BankMtkRequestResp.Builder builder = SubscribeResp.BankMtkRequestResp.newBuilder();
        IDataset biz = result.getDataset(1);
        return builder.setExtsystemId(biz.getInt("extsystem_id"))
                .setThirdReff(biz.getString("third_reff"))
                .setEntrustNo(biz.getInt("entrust_no"))
                .setEntrustFailCode(biz.getInt("entrust_fail_code"))
                .setFailCause(biz.getString("fail_cause"))
                .setRiskNo(biz.getInt("risk_serial_no"))
                .setStockCode(biz.getString("stock_code"))
                .setEntrustDirection(biz.getString("entrust_direction"))
                .setRiskNo(biz.getInt("risk_no"))
                .setRiskType(biz.getString("risk_type"))
                .setRiskSummary(biz.getString("risk_type"))
                .setRiskOperation(biz.getString("risk_operation"))
                .setRemarkShort(biz.getString("remark_short"))
                .setRiskThresholdValue(biz.getDouble("risk_threshold_value"))
                .setRiskValue(biz.getDouble("risk_value"))
                .build();


    }

    public static SubscribeResp.BankMtkRequestResp toBankMtkRequestResp() {
        return SubscribeResp.BankMtkRequestResp.newBuilder()
                .setExtsystemId(123)
                .setThirdReff("321")
                .setEntrustNo(34343)
                .setEntrustFailCode(232)
                .setFailCause("fail_cause")
                .setRiskNo(777)
                .setStockCode("xt4354")
                .setEntrustDirection("a")
                .setRiskNo(2452389)
                .setRiskType("1")
                .setRiskSummary("asfdd")
                .setRiskOperation("gsdfsdfg")
                .setRemarkShort("dfsgasdfga")
                .setRiskThresholdValue(483523.2343)
                .setRiskValue(78789)
                .build();
    }
}
