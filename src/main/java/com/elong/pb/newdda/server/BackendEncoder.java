package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/3/1.
 */
public class BackendEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(BackendEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {



    }

}
