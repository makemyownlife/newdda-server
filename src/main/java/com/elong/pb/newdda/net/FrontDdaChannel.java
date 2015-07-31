package com.elong.pb.newdda.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 前端链接
 * Created by zhangyong on 15/7/23.
 */
public class FrontDdaChannel extends DdaChannel {

    private final AcceptIdGenerator acceptIdGenerator = new AcceptIdGenerator();

    private Channel channel;

    //前端编号
    private Long id;

    private byte[] seed;

    private volatile boolean isAuthenticated;

    public FrontDdaChannel(Channel channel){
        this.channel = channel;
        this.id = acceptIdGenerator.getId();
        this.isAuthenticated = false;
    }

    @Override
    public void write(Object message) {
        this.channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
            }
        });
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

    //===============================================get set method =======================================================
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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

}
