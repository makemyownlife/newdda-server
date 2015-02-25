package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.BinaryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

/**
 * Created by zhangyong on 15/2/13.
 */
public class FrontDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger logger = LoggerFactory.getLogger(FrontDecoder.class);

    //最大的是16M
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
            //若是 ,验证,则直接返回空，若是查询，则传递到 FrontServerHandler
            FrontDdaChannel frontDdaChannel = FrontClient.getInstance().getFrontDdaChannel(channel);
            BinaryPacket binaryPacket = frontDdaChannel.handle(frame.nioBuffer());
            return binaryPacket;
        } catch (Exception e) {
            logger.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            // 这里关闭后， 会在pipeline中产生事件，通过具体的close事件来清理数据结构
            RemotingUtil.closeChannel(ctx.channel());
        }
        return null;
    }

}