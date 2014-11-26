package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 14/11/22.
 */
public class NettyFrontEncoder extends MessageToByteEncoder{

    private Logger logger = LoggerFactory.getLogger(NettyFrontEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        logger.info("msg==" + msg);
        System.out.println(1111);
    }

}
