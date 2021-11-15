package com.sumscope.qt.cbt.connector.ufx.config;

import com.hundsun.mcapi.MCServers;
import com.hundsun.mcapi.interfaces.ISubscriber;
import com.hundsun.mcapi.subscribe.MCSubscribeParameter;
import com.hundsun.t2sdk.common.core.context.ContextUtil;
import com.hundsun.t2sdk.common.share.dataset.DatasetService;
import com.hundsun.t2sdk.impl.client.T2Services;
import com.hundsun.t2sdk.interfaces.IClient;
import com.hundsun.t2sdk.interfaces.T2SDKException;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.hundsun.t2sdk.interfaces.share.event.EventReturnCode;
import com.hundsun.t2sdk.interfaces.share.event.EventTagdef;
import com.hundsun.t2sdk.interfaces.share.event.EventType;
import com.hundsun.t2sdk.interfaces.share.event.IEvent;
import com.sumscope.qt.cbt.connector.ufx.util.UFXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class UFXAppConnector {

	public IClient client = null;
	public T2Services server = null;
	public int TIMEOUT = 10000;
	public String session = null;
	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public int subscribeid;
	public ISubscriber subscriber;
	private final UfxConfig ufxConfig;

	private static UFXAppConnector ufxAppConnector;

	private UFXAppConnector(UfxConfig ufxConfig) {
		this.ufxConfig = ufxConfig;
		server = new T2Services();
		server.setT2sdkConfigString(ufxConfig.getT2sdkConfigPath());
		UFXUtils.OPERATOR_NO = ufxConfig.getUsername();
		UFXUtils.PASSWD = ufxConfig.getPassword();
		try {
			Connect();
			login();
		} catch (Exception e) {
			try {
				this.DisConnect();
			} catch (Exception e1) {
				logger.info(e1.getMessage());
			}
			logger.info(e.getMessage());
		}
	}

	public static synchronized UFXAppConnector getInstance(UfxConfig ufxConfig) {
		if (ufxAppConnector == null) {
			ufxAppConnector = new UFXAppConnector(ufxConfig);
		}
		return ufxAppConnector;
	}

	/**
	 * 链接后台 订阅主推消息
	 *
	 * @throws Exception
	 */
	public void Connect() throws Exception {
		logger.info("链接后台服务 订阅主推消息");
		server.init();
		server.start();
		client = server.getClient("as_ufx");
		MCServers.MCInit();
		subscriber = MCServers.GetSubscriber();
		MCSubscribeParameter subParam = new MCSubscribeParameter();
		subParam.SetTopicName("ufx_topic"); // ufx成交回报固定主题
		subParam.SetFromNow(true);
		subParam.SetReplace(false);
		subParam.SetFilter("operator_no", ufxConfig.getUsername());
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("login_operator_no");
		dataset.addColumn("password");
		dataset.appendRow();
		dataset.updateString("login_operator_no", ufxConfig.getUsername());
		dataset.updateString("password", ufxConfig.getPassword());
		subParam.SetBizCheck(dataset);
		subscribeid = subscriber.SubscribeTopic(subParam, 3000);
		if (subscribeid < 0) {
			logger.info("订阅主题失败 ret[" + subscribeid + "]");
			//throw new Exception("订阅主题失败 ret[" + subscribeid + "]");
		}else{
			logger.info("订阅主题成功 ret[" + subscribeid + "]");
		}

	}

	/**
	 * 取消订阅 断开链接
	 *
	 * @throws Exception
	 */
	public void DisConnect() throws Exception {
		logger.info("后台链接断开");
		int ret = subscriber.CancelSubscribeTopic(subscribeid);
		if (ret < 0) {
			throw new Exception("取消订阅主题失败 ret[" + ret + "]");
		}
		MCServers.Destroy();
		server.stop();
	}

	/**
	 * 登陆获取session
	 *
	 * @throws Exception
	 */
	public void login() throws Exception {
		logger.info("开始登陆");
		IDatasets result = callSerivce(10001, UFXUtils.GetLoginPack());
		IDataset head = result.getDataset(0);
		int errCode = head.getInt("ErrorCode");
		if (errCode != 0) {
			throw new Exception(head.getString("ErrorMsg"));
		} else {
			if (head.getInt("DataCount") != 0) {
				IDataset biz = result.getDataset(1);
				session = biz.getString("user_token");
			}
		}
		logger.info("登陆成功获取Session[" + session + "]");
	}

//	/**
//	 * 普通买卖委托(单笔股票买卖)
//	 */
//	public String entrust() throws Exception {
//		logger.info("委托下达");
//		IDatasets result = callSerivce(91001, UFXUtils.GetEntrustPack(session));
//		UFXUtils.PrintMsg(result);
//		IDataset biz = result.getDataset(1);
//		return biz.getString("entrust_no");
//	}
//
//	/**
//	 * 委托查询
//	 */
//	public void entrustQry(String enrtustno) throws Exception {
//		logger.info("委托查询");
//		//同步调用
//		//IDatasets result = CallSerivce(32001,
//		//UFXUtils.GetEntrustQryPack(session, enrtustno));
//		//UFXUtils.PrintMsg(result);
//		//异步调用
//		callSerivceAsync(32001,UFXUtils.GetEntrustQryPack(session, enrtustno));
//	}
//
//	/**
//	 * 成交查询
//	 */
//	public void dealQry(String enrtustno) throws Exception {
//		logger.info("成交查询");
//		IDatasets result = callSerivce(33001,
//				UFXUtils.GetDealQryPack(session, enrtustno));
//		UFXUtils.PrintMsg(result);
//	}

//	/**
//	 * 撤单
//	 */
//	public void withdraw(String enrtustno) throws Exception {
//		logger.info("委托撤单");
//		IDatasets result = CallSerivce(91101,
//				UFXUtils.GetWithdrawPack(session, enrtustno));
//		UFXUtils.PrintMsg(result);
//	}

	/**
	 * 同步调用
	 *
	 * @param funcno
	 * @param dataset
	 * @return
	 * @throws T2SDKException
	 */
	public IDatasets callSerivce(int funcno, IDataset dataset)
			throws T2SDKException {
		IDatasets result = null;

		IEvent event = ContextUtil.getServiceContext().getEventFactory()
				.getEventByAlias(String.valueOf(funcno), EventType.ET_REQUEST);
		event.putEventData(dataset);
		IEvent rsp = client.sendReceive(event, TIMEOUT);
		UFXUtils.PrintMsg(rsp.getEventDatas());
		// 先判断返回值
		if (rsp.getReturnCode() != EventReturnCode.I_OK) { // 返回错误
			throw new T2SDKException(rsp.getErrorNo(), rsp.getErrorInfo());
		} else {
			result = rsp.getEventDatas();
		}
		return result;
	}

	/**
	 * 异步调用
	 *
	 * @param funcno
	 * @param dataset
	 * @return
	 * @throws T2SDKException
	 */
	public void callSerivceAsync(int funcno, IDataset dataset)
			throws T2SDKException {
		IEvent event = ContextUtil.getServiceContext().getEventFactory()
				.getEventByAlias(String.valueOf(funcno), EventType.ET_REQUEST);
		event.putEventData(dataset);
		event.setIntegerAttributeValue(EventTagdef.TAG_SENDERID, 5);
		client.send(event);
		// 先判断返回值
	}

}
