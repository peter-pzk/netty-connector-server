package com.sumscope.qt.cbt.connector.ufx.util;

import com.hundsun.t2sdk.common.share.dataset.DatasetService;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeReq;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class UFXUtils {

	public static String OPERATOR_NO = "1000";
	public static String PASSWD = "0";
	public static String ACCOUNT_CODE = "106000";
	public static String COMBI_NO = "106A1";
	public static String MARKET_NO = "1";
	public static String STOCK_CODE = "801354";
	public static String ENTRUST_PRICE = "14";
	public static String ENTRUST_AMOUNT = "1000";

	private static final Logger logger = Logger.getLogger("UFXUtil");

	public static void PrintMsg(IDatasets result) {
		StringBuilder sb = new StringBuilder();
		int datasetCount = result.getDatasetCount();
		// 遍历所有的结果集
		for (int i = 0; i < datasetCount; i++) {
			sb.append("===============================================\n");
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(i);
			int columnCount = ds.getColumnCount();
			// 遍历单个结果集列信息
			for (int j = 1; j <= columnCount; j++) {
				sb.append(String.format("%20s", ds.getColumnName(j)));
				sb.append("|");
			}
			sb.append("\n");
			// 遍历单个结果集记录，遍历前首先将指针置到开始
			ds.beforeFirst();
			while (ds.hasNext()) {
				sb.append("\n");
				ds.next();
				for (int j = 1; j <= columnCount; j++) {
					sb.append(String.format("%20s", ds.getString(j)));
					sb.append("|");
				}
			}
		}
		sb.append("\n");
		logger.info(sb.toString());
	}

	/**
	 * 登陆消息(10001)
	 * 
	 * @return
	 */
	public static IDataset GetLoginPack() throws SocketException, UnknownHostException {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("operator_no");
		dataset.addColumn("password");
		dataset.addColumn("mac_address");
		dataset.addColumn("op_station");
		dataset.addColumn("ip_address");
		dataset.addColumn("authorization_id");
		dataset.addColumn("login_time");
		dataset.addColumn("verification_code");
		dataset.appendRow();
		dataset.updateString("operator_no", OPERATOR_NO);
		dataset.updateString("password", PASSWD);
		dataset.updateString("mac_address", getLocalMac());
		dataset.updateString("op_station", OPERATOR_NO);
		dataset.updateString("ip_address", InetAddress.getLocalHost().getHostAddress());
		dataset.updateString("authorization_id", OPERATOR_NO);
		dataset.updateString("login_time", new Date().toString());
		dataset.updateString("verification_code", "");
		return dataset;
	}



	/**
	 * 银行间做市请求回复撤销(91146)
	 * func_bank_mtk_request_cancel
	 * @return
	 */
	public static IDataset GetBankMtkRequestCancel(String session, String enrtustno) {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		//令牌号
		dataset.addColumn("user_token");
		//委托序号
		dataset.addColumn("entrust_no");

		dataset.appendRow();
		dataset.updateString("user_token", session);
		dataset.updateString("entrust_no", enrtustno);

		return dataset;
	}


	/**
	 * 银行间做市请求回复委托 (91144)
	 */
	public static IDataset GetBankMtkRequest(String session, SubscribeReq.BankMtkRequest args) {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		//用户口令
		dataset.addColumn("user_token");
		//账户编号
		dataset.addColumn("account_code");
		//组合编号
		dataset.addColumn("combi_no");
		//投资类型
		dataset.addColumn("invest_type");
		//请求编号
		dataset.addColumn("request_id");
		//本方交易员
		dataset.addColumn("owner_operator_name");
		//净价
		dataset.addColumn("net_price");
		//券面总额
		dataset.addColumn("face_balance");
		//有效时间
		dataset.addColumn("valid_time");
		//第三方系统自定义号
		dataset.addColumn("extsystem_id");
		//第三方系统自定义说明
		dataset.addColumn("third_reff");

		dataset.appendRow();
		dataset.updateString("user_token", session);
		dataset.updateString("account_code", args.getAccountCode());
		dataset.updateString("combi_no", args.getCombiNo());
		dataset.updateString("invest_type", args.getInvestType());
		dataset.updateString("request_id", args.getRequestId());
		dataset.updateString("owner_operator_name", args.getOwnerOperatorName());
		dataset.updateDouble("net_price", args.getNetPrice());
		dataset.updateDouble("face_balance", args.getFaceBalance());
		dataset.updateInt("valid_time", args.getValidTime());
		dataset.updateInt("extsystem_id", args.getExtsystemId());
		dataset.updateString("third_reff", args.getThirdReff());
		return dataset;
	}


	public static String getLocalMac() throws UnknownHostException, SocketException {
		InetAddress ia = InetAddress.getLocalHost();
		// TODO Auto-generated method stub
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		//System.out.println("mac数组长度："+mac.length);
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			//System.out.println("每8位:"+str);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}


	/**
	 * 普通买卖委托消息 (91001)
	 *
	 * @return
	 */
	public static IDataset GetEntrustPack(String session) {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("user_token");
		dataset.addColumn("batch_no");
		dataset.addColumn("account_code");
		dataset.addColumn("asset_no");
		dataset.addColumn("combi_no");
		dataset.addColumn("instance_no");
		dataset.addColumn("stockholder_id");
		dataset.addColumn("report_seat");
		dataset.addColumn("market_no");
		dataset.addColumn("stock_code");
		dataset.addColumn("entrust_direction");
		dataset.addColumn("price_type");
		dataset.addColumn("entrust_price");
		dataset.addColumn("entrust_amount");
		dataset.addColumn("invest_type");
		dataset.addColumn("extsystem_id");
		dataset.addColumn("third_reff");

		dataset.appendRow();
		dataset.updateString("user_token", session);
		dataset.updateString("batch_no", "");
		dataset.updateString("account_code", ACCOUNT_CODE);
		dataset.updateString("asset_no", "");
		dataset.updateString("combi_no", COMBI_NO);
		dataset.updateString("instance_no", "");
		dataset.updateString("stockholder_id", "");
		dataset.updateString("report_seat", "");
		dataset.updateString("market_no", MARKET_NO);
		dataset.updateString("stock_code", STOCK_CODE);
		dataset.updateString("entrust_direction", "3");
		dataset.updateString("price_type", "0");
		dataset.updateString("entrust_price", ENTRUST_PRICE);
		dataset.updateString("entrust_amount", ENTRUST_AMOUNT);
		dataset.updateString("invest_type", "");
		dataset.updateString("extsystem_id", "hundsun");
		dataset.updateString("third_reff", "demo");
		return dataset;
	}

	/**
	 * 委托查询(32001)
	 *
	 * @return
	 */
	public static IDataset GetEntrustQryPack(String session, String enrtustno) {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("user_token");
		dataset.addColumn("account_code");
		dataset.addColumn("combi_no");
		dataset.addColumn("entrust_no");

		dataset.appendRow();
		dataset.updateString("user_token", session);
		dataset.updateString("account_code", ACCOUNT_CODE);
		dataset.updateString("combi_no", COMBI_NO);
		dataset.updateString("entrust_no", enrtustno);

		return dataset;
	}

	/**
	 * 成交查询(33001)
	 *
	 * @return
	 */
	public static IDataset GetDealQryPack(String session, String enrtustno) {
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("user_token");
		dataset.addColumn("account_code");
		dataset.addColumn("combi_no");
		dataset.addColumn("entrust_no");

		dataset.appendRow();
		dataset.updateString("user_token", session);
		dataset.updateString("account_code", ACCOUNT_CODE);
		dataset.updateString("combi_no", COMBI_NO);
		dataset.updateString("entrust_no", enrtustno);

		return dataset;
	}

	public static IDataset getDataset(Map<String,String> map){
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		Set<String> set = map.keySet();
		for (String key : set) {
			dataset.addColumn(key);
		}
		dataset.appendRow();
		for (String key : set) {
			String value = map.get(key);
			dataset.addColumn("operator_no");
			dataset.updateString(key, value);
			logger.info("IDataset map :" + key + "=" + value);
		}
		return dataset;
	}

	public static String getMsg(IDatasets result) {
		StringBuilder sb = new StringBuilder();
		int datasetCount = result.getDatasetCount();
		// 遍历所有的结果集
		for (int i = 0; i < datasetCount; i++) {
			sb.append("===============================================\n");
			// 开始读取单个结果集的信息
			IDataset ds = result.getDataset(i);
			int columnCount = ds.getColumnCount();
			// 遍历单个结果集列信息
			for (int j = 1; j <= columnCount; j++) {
				sb.append(String.format("%20s", ds.getColumnName(j)));
				sb.append("|");
			}
			sb.append("\n");
			// 遍历单个结果集记录，遍历前首先将指针置到开始
			ds.beforeFirst();
			while (ds.hasNext()) {
				sb.append("\n");
				ds.next();
				for (int j = 1; j <= columnCount; j++) {
					sb.append(String.format("%20s", ds.getString(j)));
					sb.append("|");
				}
			}
		}
		sb.append("\n");
		return  sb.toString();
	}
}
