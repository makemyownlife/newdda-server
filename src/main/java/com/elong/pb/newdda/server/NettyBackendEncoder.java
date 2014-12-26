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
 * 后端链接发送数据给 mysql server
 */
public class NettyBackendEncoder extends MessageToByteEncoder {

    private static Logger logger = LoggerFactory.getLogger(NettyBackendEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            Packet packet = (Packet) msg;
            logger.info("msg==" + msg);
            ByteBuffer byteBuffer = packet.encode();
            out.writeBytes(byteBuffer);
        } catch (Exception e) {
            logger.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (msg != null) {
                logger.error(msg.toString());
            }
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        }
    }

}
