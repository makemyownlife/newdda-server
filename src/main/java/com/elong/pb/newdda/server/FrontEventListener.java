package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.netty.ChannelEventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端链接事件监听器
 * Created by zhangyong on 15/7/23.
 */
public class FrontEventListener implements ChannelEventListener {

    private final static Logger logger = LoggerFactory.getLogger(FrontEventListener.class);

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {

    }

}
