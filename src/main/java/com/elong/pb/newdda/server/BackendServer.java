package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup eventLoopGroupWorker;

    private NettyClientConfig nettyClientConfig;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public BackendServer() {
        this.ddaConfig = new DdaConfig();
        this.nettyClientConfig = new NettyClientConfig();
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("BackendClientSelector_%d",
                        this.threadIndex.incrementAndGet()));
            }
        });
    }

    public void start() {
        logger.info("开始启动netty链接mysql服务");
        initNettyService();
        logger.info("结束启动netty链接mysql服务");

        logger.info("开始创建mysql初始化链接");
        initMysqlService();
        logger.info("结束创建mysql初始化链接");
    }

    private void initNettyService() {

    }

    private void initMysqlService() {

    }

}
