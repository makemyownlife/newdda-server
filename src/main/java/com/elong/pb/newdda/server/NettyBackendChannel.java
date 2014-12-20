package com.elong.pb.newdda.server;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.handler.backend.BackendAuthHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 14/12/18.
 * <p/>
 * 后端连接到mysql中
 */
public class NettyBackendChannel {

    private static ConnectIdGenerator connectIdGenerator = new ConnectIdGenerator();

    private volatile boolean isAuthenticated = false;

    private long threadId;

    private int charsetIndex;

    private String charset;

    private volatile int txIsolation;

    private volatile boolean autocommit;

    private volatile boolean isRunning;

    private final AtomicBoolean isClosed;

    private Long id;

    private ChannelFuture channelFuture;

    protected byte[] seed;

    private NettyHandler nettyHandler;

    public NettyBackendChannel(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
        this.id = connectIdGenerator.getId();
        this.autocommit = true;
        this.isRunning = false;
        this.isClosed = new AtomicBoolean(false);
        this.nettyHandler = new BackendAuthHandler(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channelFuture.channel();
    }

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public NettyHandler getNettyHandler() {
        return nettyHandler;
    }

    public void setNettyHandler(NettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
    }

    /**
     * 后端连接ID生成器
     *
     * @author xianmao.hexm
     */
    private static class ConnectIdGenerator {

        private static final long MAX_VALUE = Long.MAX_VALUE;

        private long connectId = 0L;
        private final Object lock = new Object();

        private long getId() {
            synchronized (lock) {
                if (connectId >= MAX_VALUE) {
                    connectId = 0L;
                }
                return ++connectId;
            }
        }
    }
}
