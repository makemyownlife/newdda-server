package com.elong.pb.newdda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 15/2/7.
 * 解析数据返回
 */
public class BackendDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(BackendDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }

}
