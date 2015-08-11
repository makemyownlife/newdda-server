package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.ExecutorUtil;
import com.elong.pb.newdda.common.NameableExecutor;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.packet.BinaryMySqlPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 真实的前端数据处理
 * Created by zhangyong on 15/7/16.
 */
public class FrontDataHandler extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(FrontDataHandler.class);

    private static NameableExecutor NETTEY_FRONT_EXECUTOR = ExecutorUtil.create("NetteyFrontExecutor", SystemConfig.DEFAULT_PROCESSORS);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final FrontDdaChannel frontDdaChannel = FrontClient.getInstance().getFrontDdaChannel(ctx.channel());
        final BinaryMySqlPacket binaryMySqlPacket = (BinaryMySqlPacket) msg;
        if (binaryMySqlPacket != null) {

        }
    }

}
