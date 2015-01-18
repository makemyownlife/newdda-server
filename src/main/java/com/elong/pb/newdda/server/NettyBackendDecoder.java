package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 14/11/22.
 * 后端数据解析
 */
public class NettyBackendDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(NettyBackendDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        NettyBackendChannel backendChannel = NettyBackendConnectManageHandler
                .getBackendConnections()
                .get(remoteAddress);
        if (backendChannel == null) {
            logger.error("error 地址:" + remoteAddress + "在后端连接池没有有注册");
            return;
        }
        if (!backendChannel.isAuthenticated()) {
            logger.info("后端链接:" + remoteAddress + " 正要注册。。");
        }
        //读取mysqlPacket
        MysqlPacket mysqlPacket = backendChannel.getNettyHandler().handle(ctx, byteBuf);
        if (mysqlPacket != null) {
            out.add(mysqlPacket);
        }
    }

}
