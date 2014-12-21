package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.ExecutorUtil;
import com.elong.pb.newdda.common.NameableExecutor;
import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/22.
 * 中间线程池处理相关数据
 */
public class NettyBackendHandler extends SimpleChannelInboundHandler {

    private Logger logger = LoggerFactory.getLogger(NettyBackendHandler.class);

    private static NameableExecutor netteyBackendExecutor = ExecutorUtil.create("NetteyBackendExecutor", SystemConfig.DEFAULT_PROCESSORS);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("NettyBackendHandler msg==" + msg);

    }

}
