package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 14/11/22.
 * 前端数据解析
 */
public class NettyFrontDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(NettyFrontDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        //解析jdbc端传过来的信息
        String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        NettyFrontChannel nettyFrontChannel = NettyFrontConnetManageHandler.getFrontChannelTables(remoteAddress);
        if (nettyFrontChannel == null) {
            logger.error("地址:" + remoteAddress + "在连接池中没有注册，需要删除该连接");
            RemotingUtil.closeChannel(ctx.channel());
            return;
        }
        //解析数据 第一步 判断有无认证
        if (!nettyFrontChannel.isAuthenticated()) {
            logger.warn("地址:" + remoteAddress + " 没有认证,需要认证");
        }
        nettyFrontChannel.getNettyHandler().handle(byteBuf);
    }

}
