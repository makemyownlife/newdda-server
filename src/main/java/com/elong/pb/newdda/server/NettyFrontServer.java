package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.NettyServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyong on 14/11/22.
 * Netty前端服务
 */
public class NettyFrontServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyFrontServer.class);

    private NettyServerConfig nettyServerConfig;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupWorker;

    private EventLoopGroup eventLoopGroupBoss;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyFrontServer(final NettyServerConfig nettyServerConfig) {
        this.serverBootstrap = new ServerBootstrap();
        this.nettyServerConfig = nettyServerConfig;
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,
                        String.format("NettyFrontBossSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });

        this.eventLoopGroupWorker =
                new NioEventLoopGroup(5, new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    private int threadTotal = nettyServerConfig.getServerSelectorThreads();

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("NettyFrontServerSelector_%d_%d", threadTotal,
                                this.threadIndex.incrementAndGet()));
                    }
                });

    }

    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                nettyServerConfig.getServerWorkerThreads(), //
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        ServerBootstrap childHandler = //
                this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                        .channel(NioServerSocketChannel.class)
                        //
                        .option(ChannelOption.SO_BACKLOG, 1024)
                                //
                        .option(ChannelOption.SO_REUSEADDR, true)
                                //
                        .option(ChannelOption.SO_KEEPALIVE, false)
                                //
                        .childOption(ChannelOption.TCP_NODELAY, true)
                                //
                        .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                                //
                        .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                                //
                        .localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(
                                        //
                                        defaultEventExecutorGroup, //
                                        new NettyFrontEncoder(), //
                                        new NettyFrontDecoder(), //
                                        new IdleStateHandler(0, 0, nettyServerConfig
                                                .getServerChannelMaxIdleTimeSeconds()),//
                                    //  new NettyConnetManageHandler(),
                                        new NettyFrontHandler());
                            }
                        });

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }
    }

}
