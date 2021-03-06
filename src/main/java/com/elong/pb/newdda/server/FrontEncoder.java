package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.net.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 编码包(将对象转成字节数组发送)
 * Created by zhangyong on 15/7/15.
 */
public class FrontEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(FrontEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            Packet packet = (Packet) msg;
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
