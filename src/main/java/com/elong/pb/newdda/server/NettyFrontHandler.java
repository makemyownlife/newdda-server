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
public class NettyFrontHandler extends SimpleChannelInboundHandler {

    private Logger logger = LoggerFactory.getLogger(NettyFrontHandler.class);

    private static NameableExecutor netteyFrontExecutor = ExecutorUtil.create("NetteyFrontExecutor", SystemConfig.DEFAULT_PROCESSORS);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Object msg) throws Exception {
        String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        final NettyFrontChannel nettyFrontChannel = NettyFrontConnetManageHandler.getFrontChannelTables(remoteAddress);
        //解析数据 第一步 判断有无认证
        if (!nettyFrontChannel.isAuthenticated()) {
            logger.warn("地址:" + remoteAddress + " 认证收到auth包，正在处理");
        }
        final MysqlPacket mysqlPacket = (MysqlPacket) msg;
        netteyFrontExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Packet packet = nettyFrontChannel.getNettyHandler().handle(mysqlPacket);
                    if (packet != null) {
                        ctx.writeAndFlush(packet);
                    }
                } catch (Exception e) {
                    logger.error("NettyFrontHandler handle error:" + e.getMessage());
                }
            }
        });
    }


}
