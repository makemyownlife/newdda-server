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
 * Created by zhangyong on 14/11/22.
 * Netty后端服务(连接mysql服务)
 */
public class NettyBackendServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyBackendServer.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup eventLoopGroupWorker;

    private NettyClientConfig nettyClientConfig;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyBackendServer(NettyClientConfig nettyClientConfig) {
        this.nettyClientConfig = nettyClientConfig;
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
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                nettyClientConfig.getClientWorkerThreads(), //
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyBackendClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });
        Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyBackendEncoder(),
                                new NettyBackendDecoder(),
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),//
                                new NettyBackendConnectManageHandler(),
                                new NettyBackendHandler());
                    }
                });
    }

    public void stop() {

    }


}
