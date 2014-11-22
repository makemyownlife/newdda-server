package com.elong.pb.newdda.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/22.
 * 前端连接管理
 */
public class NettyFrontConnetManageHandler extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyFrontConnetManageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("channelRead0...");
    }

}
