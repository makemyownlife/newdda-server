package com.elong.pb.newdda.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyong on 14/11/22.
 * Netty后端服务(连接mysql服务)
 */
public class NettyBackendServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyBackendServer.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup eventLoopGroupWorker;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyBackendServer() {
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBackendClientSelector_%d",
                        this.threadIndex.incrementAndGet()));
            }
        });


    }

    public void start() {

    }

    public void stop() {

    }


}
