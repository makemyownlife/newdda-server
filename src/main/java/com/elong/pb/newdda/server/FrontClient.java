package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.ServiceThread;
import com.elong.pb.newdda.common.netty.ChannelEventListener;
import com.elong.pb.newdda.common.netty.NettyEvent;
import com.elong.pb.newdda.config.NettyServerConfig;
import com.elong.pb.newdda.net.FrontDdaChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管理 客户端链接(前端数据相关的内容)
 * Created by zhangyong on 15/7/12.
 */
public class FrontClient {

    private final static Logger logger = LoggerFactory.getLogger(FrontClient.class);

    private static FrontClient INSTANCE = new FrontClient();

    public static FrontClient getInstance() {
        return INSTANCE;
    }

    private FrontEventExecuter frontEventExecuter;

    private NettyServerConfig nettyServerConfig;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupWorker;

    private EventLoopGroup eventLoopGroupBoss;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public FrontClient() {
        this.nettyServerConfig = new NettyServerConfig();
        this.frontEventExecuter = new FrontEventExecuter(new FrontEventListener());
        this.serverBootstrap = new ServerBootstrap();
        //boss线程 用来处理链接的接收(1个单线程用来处理即可)
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,
                        String.format("NettyFrontBossSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });
        //用来处理链接事件
        this.eventLoopGroupWorker =
                new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    private int threadTotal = nettyServerConfig.getServerSelectorThreads();
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("NettyFrontServerSelector_%d_%d", threadTotal,
                                this.threadIndex.incrementAndGet()));
                    }
                });
        //处理读写线程 会比selector多一些
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                nettyServerConfig.getServerWorkerThreads(),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyFrontServerWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });
    }

    //启动客户端(链接)
    public void start() {
        ServerBootstrap childHandler =
                this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                        .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                        .localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(
                                        defaultEventExecutorGroup,
                                        new FrontDecoder(),
                                        new FrontEncoder(),
                                        new IdleStateHandler(
                                                0,
                                                0,
                                                nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                        new FrontEventHandler(),
                                        new FrontDataHandler()
                                );
                            }
                        });
        //启动前端服务
        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            logger.info("本地服务服务端开启{}", addr);
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        //添加前端事件处理线程启动 （当前仅仅支持链接事件,以后可以添加）
        frontEventExecuter.start();
    }

    class FrontEventExecuter extends ServiceThread {
        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<NettyEvent>();
        private final int MaxSize = 100000;
        private ChannelEventListener channelEventListener;
        public FrontEventExecuter(ChannelEventListener channelEventListener) {
            this.channelEventListener = channelEventListener;
        }
        public void putNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= MaxSize) {
                this.eventQueue.add(event);
            } else {
                logger.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }
        @Override
        public void run() {
            logger.info(this.getServiceName() + " service started");
            final ChannelEventListener listener = this.channelEventListener;
            while (!this.isStoped()) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    logger.warn(this.getServiceName() + " service has exception. ", e);
                }
            }
            logger.info(this.getServiceName() + " service end");
        }
        @Override
        public String getServiceName() {
            return "FrontEventExecuter";
        }
    }
    //========================================= 前端链接管理 ========================================================================
    private static ConcurrentHashMap<Channel, FrontDdaChannel> FRONT_CHANNELS = new ConcurrentHashMap<Channel, FrontDdaChannel>(); //前端链接存储

    //添加相关的事件(前端事件管理器来用)
    public void addEvent(NettyEvent nettyEvent) {
        this.frontEventExecuter.putNettyEvent(nettyEvent);
    }

    //添加前端链接
    public void addFrontDdaChannel(FrontDdaChannel frontDdaChannel) {
        if (frontDdaChannel != null) {
            if (!FRONT_CHANNELS.containsKey(frontDdaChannel.getChannel())) {
                Channel channel = frontDdaChannel.getChannel();
                FRONT_CHANNELS.put(channel, frontDdaChannel);
            }
        }
    }

    public FrontDdaChannel getFrontDdaChannel(Channel channel) {
        return FRONT_CHANNELS.get(channel);
    }

}
