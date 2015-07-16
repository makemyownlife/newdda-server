package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 编码包(将对象转成字节数组发送)
 * Created by zhangyong on 15/7/15.
 */
public class FrontEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(FrontEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        
    }

}
