package com.elong.pb.newdda.server;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.handler.front.FrontAuthHandler;
import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 14/11/24.
 * <p/>
 * 前端连接
 */
public class NettyFrontChannel {

    private static AcceptIdGenerator acceptIdGenerator = new AcceptIdGenerator();

    private volatile boolean isAuthenticated = false;

    private Long id;

    private Channel channel;

    protected byte[] seed;

    private  NettyHandler nettyHandler;

    public NettyFrontChannel(Channel channel) {
        this.channel = channel;
        this.id = acceptIdGenerator.getId();
        nettyHandler = new FrontAuthHandler(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
     * 前端连接ID生成器
     *
     * @author xianmao.hexm
     */
    private static class AcceptIdGenerator {

        private static final long MAX_VALUE = 0xffffffffL;

        private long acceptId = 0L;
        private final Object lock = new Object();

        private long getId() {
            synchronized (lock) {
                if (acceptId >= MAX_VALUE) {
                    acceptId = 0L;
                }
                return ++acceptId;
            }
        }

    }

}
