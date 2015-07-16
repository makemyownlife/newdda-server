package com.elong.pb.newdda.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 真实的前端数据处理
 * Created by zhangyong on 15/7/16.
 */
public class FrontDataHandler extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(FrontDataHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        
    }

}
