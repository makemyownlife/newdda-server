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
 * 后端控制器（链接mysql相关的服务）
 * Created by zhangyong on 15/8/3.
 */
public class BackendClient {

    private final static Logger logger = LoggerFactory.getLogger(BackendClient.class);

    private static BackendClient INSTANCE = new BackendClient();

    private final Bootstrap bootstrap = new Bootstrap();

    private EventLoopGroup eventLoopGroupWorker = null;

    private NettyClientConfig nettyClientConfig = new NettyClientConfig();

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public static BackendClient getInstance() {
        return INSTANCE;
    }

    public BackendClient() {

    }

    public void start() {
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("BackendClientSelector_%d",
                        this.threadIndex.incrementAndGet()));
            }
        });
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                nettyClientConfig.getClientWorkerThreads(),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "BackendClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });
        Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                        //  添加超时时间
                        // .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,(int)nettyClientConfig.getConnectTimeoutMillis())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                //encoder 在 decoder 之前
                                new BackendEncoder(),
                                new BackendDecoder(),
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                                new BackendEventHandler()
                        );
                    }
                });
    }


}
