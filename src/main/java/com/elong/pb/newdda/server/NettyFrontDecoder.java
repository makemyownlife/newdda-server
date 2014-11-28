package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 14/11/22.
 */
public class NettyFrontDecoder extends ByteToMessageDecoder{

    private Logger logger = LoggerFactory.getLogger(NettyFrontDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(logger.isDebugEnabled()){
            logger.debug("对象：" + in);
            logger.debug("byteBuf.readableBytes():" + in.readableBytes());
        }
    }

}
