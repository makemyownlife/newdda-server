package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.net.BackendDdaChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by zhangyong on 15/2/7.
 * 解析数据返回
 */
public class BackendDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger logger = LoggerFactory.getLogger(BackendDecoder.class);

    //最大的是16M
    private static final Integer MAX_PACKET_SIZE = 1024 * 1024 * 16;

    public BackendDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_SIZE, 0, 3, 1, 0, true);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Channel channel = ctx.channel();
        BackendDdaChannel backendDdaChannel = BackendClient.getInstance().getMappingBackendChannel(channel);
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            ByteBuffer byteBuffer = frame.nioBuffer();

        } catch (Exception e) {
            logger.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }

}
