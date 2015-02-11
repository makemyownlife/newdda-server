package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.config.NettyClientConfig;
import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.net.ChannelWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyong on 15/2/7.
 * 发送命令相关
 */
public class BackendClient {

    private final static Logger logger = LoggerFactory.getLogger(BackendClient.class);

    private AtomicBoolean INIT = new AtomicBoolean(false);

    private static BackendClient INSTANCE = new BackendClient();

    private final Bootstrap bootstrap = new Bootstrap();

    private EventLoopGroup eventLoopGroupWorker = null;

    private NettyClientConfig nettyClientConfig = null;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public BackendClient() {
        if (INIT.compareAndSet(false, true)) {
            this.nettyClientConfig = new NettyClientConfig();
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
                                    new BackendDecoder(),
                                    new BackendEncoder(),
                                    new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                                    new BackendEventHandler()
                            );
                        }
                    });
        }
    }

    public static BackendClient getInstance() {
        return INSTANCE;
    }

    //========================================= 相关的基本方法=========================================================================

    //所有后端应用映射
    private static ConcurrentHashMap<Channel, BackendDdaChannel> BACKEND_MAPPING = new ConcurrentHashMap<Channel, BackendDdaChannel>();

    //所有后端应用链接 是否初始化后端链接
    private static ConcurrentHashMap<Channel, CountDownLatch> BACKEND_MAPPING_INIT = new ConcurrentHashMap<Channel, CountDownLatch>();

    public ChannelWrapper createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr));
            BACKEND_MAPPING_INIT.put(channelFuture.channel(), countDownLatch);
            logger.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
            cw = new ChannelWrapper(channelFuture);
        } catch (Exception e) {
            logger.error("createChannel: create channel exception", e);
            if (cw != null) {
                clearMappingBackendInitFlag(cw.getChannel());
            }
        }
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (!channelFuture.awaitUninterruptibly(this.nettyClientConfig.getConnectTimeoutMillis())) {
                logger.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.nettyClientConfig.getConnectTimeoutMillis(), channelFuture.toString());
            } else {
                if (!cw.isOK()) {
                    logger.warn("createChannel: connect remote host[" + addr + "] failed," + channelFuture.toString(), channelFuture.cause());
                    clearMappingBackendInitFlag(cw.getChannel());
                } else {
                    logger.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw;
                }
            }
        }
        //将未完成的cw 放入delay队列中 定时销毁(待写。。。需要测试)
        return null;
    }

    public void addBackendChannelMapping(Channel channel, BackendDdaChannel backendDdaChannel) {
        if (BACKEND_MAPPING.containsKey(channel)) {
            logger.error("backendchannel mapping already exist channel:{}", channel);
            return;
        }
        BACKEND_MAPPING.put(channel, backendDdaChannel);
        clearMappingBackendInitFlag(channel);
    }

    //保证取得信息的时候
    public BackendDdaChannel getMappingBackendChannel(Channel channel) {
        CountDownLatch countDownLatch = BACKEND_MAPPING_INIT.get(channel);
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.error("getMappingBackendChannel await error :" + e.getMessage());
            }
        }
        return BACKEND_MAPPING.get(channel);
    }

    private void clearMappingBackendInitFlag(Channel channel) {
        CountDownLatch countDownLatch = BACKEND_MAPPING_INIT.get(channel);
        if (countDownLatch != null) {
            countDownLatch.countDown();
            BACKEND_MAPPING_INIT.remove(channel);
            countDownLatch = null;
        }
    }

    public void removeBackendChannel(Channel channel) {
        BACKEND_MAPPING.remove(channel);
        RemotingUtil.closeChannel(channel);
    }

}
