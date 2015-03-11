package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.ExecutorUtil;
import com.elong.pb.newdda.common.NameableExecutor;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.BinaryPacket;
import com.elong.pb.newdda.packet.ErrorPacket;
import com.elong.pb.newdda.packet.MysqlPacket;
import com.elong.pb.newdda.packet.factory.ErrorPacketFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/2/13.
 */
public class FrontServerHandler extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(FrontServerHandler.class);

    private static NameableExecutor NETTEY_FRONT_EXECUTOR = ExecutorUtil.create("NetteyFrontExecutor", SystemConfig.DEFAULT_PROCESSORS * 2);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final FrontDdaChannel frontDdaChannel = FrontClient.getInstance().getFrontDdaChannel(ctx.channel());
        final BinaryPacket binaryPacket = (BinaryPacket) msg;
        if (binaryPacket != null) {
            NETTEY_FRONT_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ByteBuffer byteBuffer = binaryPacket.getByteBuffer();
                        System.out.println("byteBuffer.get(4)=" + byteBuffer.get(4));
                        switch (byteBuffer.get(4)) {
                            case MysqlPacket.COM_QUERY:
                                frontDdaChannel.getFrontQueryHandler().handle(byteBuffer);
                                break;
                            default:
                                ErrorPacket errorPacket = ErrorPacketFactory.errorMessage(
                                        ErrorCode.ER_UNKNOWN_COM_ERROR,
                                        "Unknown command");
                                frontDdaChannel.write(errorPacket);
                        }
                    } catch (Exception e) {
                        logger.error(" ", e);
                    }
                }
            });
        }
    }

}
