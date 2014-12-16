package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.net.mysql.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/22.
 */
public class NettyBackendEncoder extends MessageToByteEncoder {

    private static Logger logger = LoggerFactory.getLogger(NettyBackendEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

    }

}
