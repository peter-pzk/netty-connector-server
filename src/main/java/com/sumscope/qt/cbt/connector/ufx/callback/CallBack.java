package com.sumscope.qt.cbt.connector.ufx.callback;

import com.hundsun.t2sdk.impl.client.ClientSocket;
import com.hundsun.t2sdk.interfaces.ICallBackMethod;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.hundsun.t2sdk.interfaces.share.event.EventReturnCode;
import com.hundsun.t2sdk.interfaces.share.event.IEvent;

import java.util.logging.Logger;

public class CallBack implements ICallBackMethod{
	
	private Logger logger = Logger.getLogger("UFXUtil");
	@Override
	public void execute(IEvent arg0, ClientSocket arg1) {
		// TODO Auto-generated method stub
		//先判断返回值
		logger.info("收到异步回调消息");
		if(arg0.getReturnCode() !=  EventReturnCode.I_OK){ //返回错误
			System.out.println(arg0.getErrorNo() +" : " + arg0.getErrorInfo());
		}else{
			StringBuilder sb = new StringBuilder();
			//获得结果集
			IDatasets result = arg0.getEventDatas();
			//获得结果集总数
			int datasetCount = result.getDatasetCount();
			//遍历所有的结果集
			for (int i = 0; i < datasetCount; i++) {
				sb.append("\n" + "dataset - " + result.getDatasetName(i)
								+ "\n");
				// 开始读取单个结果集的信息
				IDataset ds = result.getDataset(i);
				int columnCount = ds.getColumnCount();
				// 遍历单个结果集列信息
				String tempStringName = "";
				for (int j = 1; j <= columnCount; j++) {
					tempStringName = ds.getColumnName(j) + "|" + ds.getColumnType(j);
					sb.append(String.format("%-30s", tempStringName));
				}
				// 遍历单个结果集记录，遍历前首先将指针置到开始
				ds.beforeFirst();
				while (ds.hasNext()) {
					sb.append("\n");
					ds.next();
					for (int j = 1; j <= columnCount; j++) {
						sb.append(String.format("%-30s", ds.getString(j)));
					}
				}
			}
		    System.out.println(sb);
		}
	}
}
