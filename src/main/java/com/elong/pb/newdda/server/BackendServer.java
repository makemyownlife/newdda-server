package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyong on 15/2/2.
 * 后端服务 启动mysql服务
 */
public class BackendServer {

    private final static Logger logger = LoggerFactory.getLogger(BackendServer.class);

    public static BackendServer INSTANCE = new BackendServer();

    public static BackendServer getInstance() {
        return INSTANCE;
    }

    private DdaConfig ddaConfig;

    public DdaConfig getDdaConfig() {
        return ddaConfig;
    }

    public BackendServer() {
        this.ddaConfig = new DdaConfig();
    }

    public void start() {

    }

    public void stop() {

    }

}
