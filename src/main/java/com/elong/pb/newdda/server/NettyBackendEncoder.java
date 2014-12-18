package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/22.
 * 后端链接发送数据给 mysql server
 */
public class NettyBackendEncoder extends MessageToByteEncoder {

    private static Logger logger = LoggerFactory.getLogger(NettyBackendEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        logger.info("msg==" + msg);
    }

}
