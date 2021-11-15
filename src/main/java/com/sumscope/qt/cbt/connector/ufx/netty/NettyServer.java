package com.sumscope.qt.cbt.connector.ufx.netty;

import com.sumscope.qt.cbt.connector.ufx.service.BankMtkRequestService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;

/**
 * @author peter pan
 * @date 2021-11-11 00:08
 */
//@Slf4j
@Component
public class NettyServer {
    public static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public final BankMtkRequestService bankMtkRequestService;

    public NettyServer(BankMtkRequestService bankMtkRequestService) {
        this.bankMtkRequestService = bankMtkRequestService;
    }

    public void start(InetSocketAddress address) {
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)  // 绑定线程池
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)  //服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .localAddress(address)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    //.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,2048, 0, 4, 0, 0,true))
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    //.addLast(new ProtobufDecoder(UserInfo.UserMsg.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new NettyServerHandler(bankMtkRequestService));
                        }
                    })//编码解码
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  //保持长连接，2小时无数据激活心跳机制

            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(address).sync();
            log.info("Netty Server start listen at：" + address.getPort());
            //关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}