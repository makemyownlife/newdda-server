package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.NettyServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 管理 客户端链接(前端数据相关的内容)
 * Created by zhangyong on 15/7/12.
 */
public class FrontClient {

    private final static Logger logger = LoggerFactory.getLogger(FrontClient.class);

    private AtomicBoolean INIT = new AtomicBoolean(false);

    private static FrontClient INSTANCE = new FrontClient();

    public static FrontClient getInstance() {
        return INSTANCE;
    }

    private NettyServerConfig nettyServerConfig;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupWorker;

    private EventLoopGroup eventLoopGroupBoss;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public FrontClient(){

    }

}
