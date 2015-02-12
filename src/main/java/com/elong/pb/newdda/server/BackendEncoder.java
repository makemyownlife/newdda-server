package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/2/7.
 * 将消息转换成字节数组
 */
public class BackendEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(BackendEncoder.class);

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
