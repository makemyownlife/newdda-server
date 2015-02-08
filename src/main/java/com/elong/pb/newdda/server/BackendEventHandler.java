package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.net.BackendChannelPool;
import com.elong.pb.newdda.net.BackendDdaChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * Created by zhangyong on 15/2/7.
 * 处理后端链接事件相关
 */
public class BackendEventHandler extends ChannelDuplexHandler {

    private final static Logger logger = LoggerFactory.getLogger(BackendEventHandler.class);

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
        closeChannelAnywhere(ctx.channel());
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
        closeChannelAnywhere(ctx.channel());
        super.close(ctx, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
        logger.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
        closeChannelAnywhere(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            if (evnet.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                closeChannelAnywhere(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    //暂时直接调用 可能会阻塞IO线程（以后修复，暂时木有时间）
    private void closeChannelAnywhere(Channel channel) {
        if (channel == null) {
            return;
        }
        BackendDdaChannel backendDdaChannel = BackendClient.getInstance().getMappingBackendChannel(channel);
        if (backendDdaChannel == null) {
            return;
        }
        BackendChannelPool pool = backendDdaChannel.getBackendChannelPool();
        if (pool != null) {
            pool.realCloseBackendChannel(backendDdaChannel, true);
        }
    }

}
