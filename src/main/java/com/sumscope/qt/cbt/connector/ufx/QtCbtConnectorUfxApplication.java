package com.sumscope.qt.cbt.connector.ufx;

import com.sumscope.qt.cbt.connector.ufx.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;

@SpringBootApplication
public class QtCbtConnectorUfxApplication implements CommandLineRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Value("${netty.port}")
    private int port;

    @Value("${netty.host}")
    private String url;
    private final NettyServer nettyServer;

    public QtCbtConnectorUfxApplication(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(QtCbtConnectorUfxApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(url, port);
        LOGGER.info("run ... ... {}", url);
        nettyServer.start(address);
    }
}
