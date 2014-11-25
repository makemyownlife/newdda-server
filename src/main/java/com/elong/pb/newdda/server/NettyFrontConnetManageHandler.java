package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RandomUtil;
import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.config.Versions;
import com.elong.pb.newdda.server.mysql.HandshakePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhangyong on 14/11/22.
 * 前端连接管理
 */
public class NettyFrontConnetManageHandler extends ChannelDuplexHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyFrontConnetManageHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
        super.channelRegistered(ctx);

        //封装前端连接
        Channel channel = ctx.channel();
        NettyFrontChannel nettyFrontChannel = new NettyFrontChannel(channel);

        //发送握手数据包
        HandshakePacket handshakePacket = new HandshakePacket();
        handshakePacket.packetId = 0;
        handshakePacket.threadId = nettyFrontChannel.getId();
        handshakePacket.protocolVersion = Versions.PROTOCOL_VERSION;
        handshakePacket.serverVersion = Versions.SERVER_VERSION;
        handshakePacket.seed = null;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
        super.channelActive(ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            if (evnet.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
                RemotingUtil.closeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
        logger.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

        RemotingUtil.closeChannel(ctx.channel());
    }

}
