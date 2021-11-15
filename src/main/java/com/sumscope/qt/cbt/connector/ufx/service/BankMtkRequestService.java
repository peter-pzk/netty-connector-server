package com.sumscope.qt.cbt.connector.ufx.service;

import com.google.gson.Gson;
import com.hundsun.t2sdk.interfaces.T2SDKException;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.sumscope.qt.cbt.connector.ufx.config.UFXAppConnector;
import com.sumscope.qt.cbt.connector.ufx.config.UfxConfig;
import com.sumscope.qt.cbt.connector.ufx.controller.manager.BankMtkRequestManager;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeReq;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeResp;
import com.sumscope.qt.cbt.connector.ufx.util.UFXFuncNoConstant;
import com.sumscope.qt.cbt.connector.ufx.util.UFXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

/**
 * @author peter pan
 * @date 2021-11-15 09:01
 */
@Service
public class BankMtkRequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public UFXAppConnector ufxAppConnector = null;
    private final UfxConfig ufxConfig;

    public BankMtkRequestService(UfxConfig ufxConfig) {
        this.ufxConfig = ufxConfig;
    }

    /**
     * 银行间做市请求回复委托(91144)
     */
    public SubscribeResp.BankMtkRequestResp getBankMtkRequest(SubscribeReq.BankMtkRequest bankMtkRequestArgs) throws T2SDKException {
        ufxAppConnector = UFXAppConnector.getInstance(this.ufxConfig);
        IDatasets result = ufxAppConnector.callSerivce(UFXFuncNoConstant.BANK_MTK_REQUEST_FUNC_NO.getFuncNo(), UFXUtils.GetBankMtkRequest(ufxAppConnector.session, bankMtkRequestArgs));
        return BankMtkRequestManager.toBankMtkRequestResp(result);
        // return BankMtkRequestManager.toBankMtkRequestResp();
    }


    public IDatasets getiDatasets(String loginMessage) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("开始登陆");
        }
        Map<String, String> map = getMap(loginMessage, 91001);
        map.computeIfAbsent("operator_no", k -> ufxConfig.getUsername());
        map.computeIfAbsent("password", k -> ufxConfig.getPassword());
        map.put("mac_address", UFXUtils.getLocalMac());
        map.put("ip_address", InetAddress.getLocalHost().getHostAddress());
        map.put("login_time", new Date().toString());

        IDatasets result = ufxAppConnector.callSerivce(10001, UFXUtils.getDataset(map));
        IDataset head = result.getDataset(0);
        if (head.getInt("DataCount") != 0) {
            IDataset biz = result.getDataset(1);
            ufxAppConnector.session = biz.getString("user_token");
        }
        LOGGER.info("登陆成功获取Session[" + ufxAppConnector.session + "]");
        return result;
    }

    public Map<String, String> getMap(String jsonMessage, Integer funcno) throws Exception {
        ufxAppConnector = UFXAppConnector.getInstance(ufxConfig);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = new Gson().fromJson(jsonMessage, type);
        if (funcno != 0) {
            if (funcno != 91001) {
                map.put("user_token", ufxAppConnector.session);
            }
        } else {
            throw new Exception(("功能号不能为空"));
        }
        return map;
    }
}
