package com.sumscope.qt.cbt.connector.ufx.netty;


import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SocketMessage;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeReq;
import com.sumscope.qt.cbt.connector.ufx.protobuf.entity.SubscribeResp;
import com.sumscope.qt.cbt.connector.ufx.service.BankMtkRequestService;
import com.sumscope.qt.cbt.connector.ufx.util.SocketMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author peter pan
 * @description: netty服务端处理类
 **/

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * 管理一个全局map，保存连接进服务端的通道数量
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    public final BankMtkRequestService bankMtkRequestService;


    public NettyServerHandler(BankMtkRequestService bankMtkRequestService) {
        this.bankMtkRequestService = bankMtkRequestService;
    }


    /**
     * @param ctx
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有客户端连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();

        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();

        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();

        System.out.println();
        //如果map中不包含此连接，就保存连接
        if (CHANNEL_MAP.containsKey(channelId)) {
            LOGGER.info("客户端【" + channelId + "】是连接状态，连接通道数量: " + CHANNEL_MAP.size());
        } else {
            //保存连接
            CHANNEL_MAP.put(channelId, ctx);

            LOGGER.info("客户端【" + channelId + "】连接netty服务器[IP:" + clientIp + "--->PORT:" + clientPort + "]");
            LOGGER.info("连接通道数量: " + CHANNEL_MAP.size());
        }
    }

    /**
     * @param ctx
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有客户端终止连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();

        String clientIp = insocket.getAddress().getHostAddress();

        ChannelId channelId = ctx.channel().id();

        //包含此客户端才去删除
        if (CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            CHANNEL_MAP.remove(channelId);

            System.out.println();
            LOGGER.info("客户端【" + channelId + "】退出netty服务器[IP:" + clientIp + "--->PORT:" + insocket.getPort() + "]");
            LOGGER.info("连接通道数量: " + CHANNEL_MAP.size());
        }
    }

    /**
     * @param ctx
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有客户端发消息会触发此函数
     * @return: void
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LOGGER.info("加载客户端报文......");
        LOGGER.info("【" + ctx.channel().id() + "】" + " :" + msg);

        // 下面可以解析数据，保存数据，生成返回报文，将需要返回报文写入write函数
        LOGGER.info("Netty Server received message loading.....");
        ByteBuf msg1 = (ByteBuf) msg;
        byte[] array = new byte[msg1.readableBytes()];
        int readerIndex = msg1.readerIndex();
        msg1.getBytes(readerIndex, array);
        //释放
        ReferenceCountUtil.release(msg1);
        // 解析字节数组
        SocketMessage socketMessage = SocketMessageUtil.decode(array);
        LOGGER.info("Netty Server pb msgName:{}", socketMessage.getMsgName());
        //调用O32接口
        //根据接口别名确定调用不同的接口
        if ("SubscribeReq.BankMtkRequestReq".equals(socketMessage.getMsgName())) {
            SubscribeResp.BankMtkRequestResp bankMtkRequestResp = bankMtkRequestService.getBankMtkRequest(SubscribeReq.BankMtkRequest.parseFrom(socketMessage.getData()));
            LOGGER.info("receive client pb data:{}", bankMtkRequestResp);
            ctx.writeAndFlush(SocketMessageUtil.encode(bankMtkRequestResp.toByteArray(), "SubscribeResp.BankMtkRequestResp"));
        } else {
            //响应客户端
            ctx.writeAndFlush(msg);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        String socketString = ctx.channel().remoteAddress().toString();

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                LOGGER.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                LOGGER.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                LOGGER.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * @param ctx
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 发生异常会触发此函数
     * @return: void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
        LOGGER.info(ctx.channel().id() + " 发生了错误,此连接被关闭" + "此时连通数量: " + CHANNEL_MAP.size());
        cause.printStackTrace();
    }
}

