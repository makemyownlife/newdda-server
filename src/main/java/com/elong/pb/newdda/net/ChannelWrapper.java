package com.elong.pb.newdda.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Created by zhangyong on 15/2/8.
 * 简单的netty channel 封装
 */
public class ChannelWrapper {

    private final ChannelFuture channelFuture;

    public ChannelWrapper(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public boolean isOK() {
        return (this.channelFuture.channel() != null && this.channelFuture.channel().isActive());
    }

    public Channel getChannel() {
        return this.channelFuture.channel();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

}
