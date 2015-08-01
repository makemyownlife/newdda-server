package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.handler.FrontAuthHandler;
import com.elong.pb.newdda.net.FrontDdaChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 前端解码器(重写)
 * Created by zhangyong on 15/7/15.
 */
public class FrontDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger logger = LoggerFactory.getLogger(FrontDecoder.class);

    private static final Integer MAX_PACKET_SIZE = 1024 * 1024 * 16;

    public FrontDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_SIZE, 0, 3, 1, 0, true);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Channel channel = ctx.channel();
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            FrontDdaChannel frontDdaChannel = FrontClient.getInstance().getFrontDdaChannel(channel);
            ByteBuffer byteBuffer = BufferUtil.transformToHeapByteBuffer(frame.nioBuffer());
            if (!frontDdaChannel.isAuthenticated()) {
                FrontAuthHandler frontAuthHandler = frontDdaChannel.getFrontAuthHandler();
                frontAuthHandler.handle(byteBuffer);
                return null;
            }
            //已经验证成功了 直接发送到 前端数据处理 类中处理

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
