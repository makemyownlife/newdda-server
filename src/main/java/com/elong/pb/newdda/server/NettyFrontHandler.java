package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/22.
 */
public class NettyFrontHandler extends SimpleChannelInboundHandler {

    private Logger logger = LoggerFactory.getLogger(NettyFrontHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("handler msg==" + msg);
        ctx.writeAndFlush(msg);
    }

}
