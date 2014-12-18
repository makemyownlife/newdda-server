package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyong on 14/12/15.
 * 后端链接管理
 */
public class NettyBackendConnectManageHandler extends ChannelDuplexHandler {

    private static Logger logger = LoggerFactory.getLogger(NettyBackendConnectManageHandler.class);

    private final static ConcurrentHashMap<Long, NettyBackendChannel>
            backendConnections =
            new ConcurrentHashMap<Long, NettyBackendChannel>();

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
        final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
        final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
        logger.info("NETTY CLIENT PIPELINE: CONNECT  {} => {}", local, remote);
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
        //closeChannel(ctx.channel());
        super.disconnect(ctx, promise);
    }


    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
        //closeChannel(ctx.channel());
        super.close(ctx, promise);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
        //  closeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            if (evnet.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                //closeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    public static ConcurrentHashMap<Long, NettyBackendChannel> getBackendConnections() {
        return backendConnections;
    }

}
