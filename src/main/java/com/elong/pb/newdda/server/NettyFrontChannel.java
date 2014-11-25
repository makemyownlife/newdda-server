package com.elong.pb.newdda.server;

import io.netty.channel.Channel;

/**
 * Created by zhangyong on 14/11/24.
 * <p/>
 * 前端连接
 */
public class NettyFrontChannel {

    private static AcceptIdGenerator acceptIdGenerator = new AcceptIdGenerator();

    private Long id ;

    private Channel channel;

    protected byte[] seed;

    public NettyFrontChannel(Channel channel){
        this.channel = channel;
        this.id = acceptIdGenerator.getId();
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
