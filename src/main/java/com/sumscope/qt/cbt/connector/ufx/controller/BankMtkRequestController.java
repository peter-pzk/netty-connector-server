package com.sumscope.qt.cbt.connector.ufx.controller;

import com.hundsun.t2sdk.interfaces.T2SDKException;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeReq;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeResp;
import com.sumscope.qt.cbt.connector.ufx.service.BankMtkRequestService;
import com.sumscope.qt.cbt.connector.ufx.util.JsonConverter;
import com.sumscope.qt.cbt.connector.ufx.util.ResponseResult;
import com.sumscope.qt.cbt.connector.ufx.util.UFXUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

import static com.sumscope.qt.cbt.connector.ufx.util.CustomConstants.REST_START_LOG_TEMPLATE;

/**
 * @author peter pan
 * @date 2021-11-09 16:07
 */
@RestController
public class BankMtkRequestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BankMtkRequestService bankMtkRequestService;

    public BankMtkRequestController(BankMtkRequestService bankMtkRequestService) {
        this.bankMtkRequestService = bankMtkRequestService;
    }

    /**
     * 登陆获取session
     */
    @ApiOperation(value = "登陆获取session ", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginMessage", value = "登录信息json字符串{\"operator_no\":\"\",\"password\":\"\",\"authorization_id\":\"\",\"op_station\":\"\"}",
                    required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseResult<String> login(String loginMessage){
        try {
            IDatasets result = bankMtkRequestService.getiDatasets(loginMessage);
            return ResponseResult.ok(UFXUtils.getMsg(result));
        }catch (Exception e){
            return ResponseResult.error(e.getMessage());
        }
    }


    @ApiOperation(value = "银行间做市请求回复委托")
    @GetMapping("/bank-mtk-request")
    public ResponseResult<SubscribeResp.BankMtkRequestResp> getBankMtkRequest(@RequestBody SubscribeReq.BankMtkRequest args) throws T2SDKException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("委托下达: BankMtkRequestArgs{}", JsonConverter.toStringOrNull(args));
            LOGGER.debug(String.format(REST_START_LOG_TEMPLATE, "/one", "GET", JsonConverter.toStringOrNull(args)));
        }
        SubscribeResp.BankMtkRequestResp view = bankMtkRequestService.getBankMtkRequest(args);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format(REST_START_LOG_TEMPLATE, "/one", "GET", JsonConverter.toStringOrNull(view)));
        }
        return ResponseResult.ok(view);
    }


}
