package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.CharsetUtil;
import com.elong.pb.newdda.common.RandomUtil;
import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.config.DdaCapability;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.config.Versions;
import com.elong.pb.newdda.net.mysql.HandshakePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by zhangyong on 14/11/22.
 * 前端连接管理
 */
public class NettyFrontConnetManageHandler extends ChannelDuplexHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyFrontConnetManageHandler.class);

    private final static ConcurrentHashMap<String /* addr */, NettyFrontChannel> frontChannelTables =
            new ConcurrentHashMap<String, NettyFrontChannel>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
        super.channelRegistered(ctx);

        //封装前端连接
        Channel channel = ctx.channel();
        NettyFrontChannel nettyFrontChannel = new NettyFrontChannel(channel);

        //假如到连接缓存中
        frontChannelTables.put(remoteAddress, nettyFrontChannel);

        // 生成认证数据
        byte[] rand1 = RandomUtil.randomBytes(8);
        byte[] rand2 = RandomUtil.randomBytes(12);

        //保存认证数据
        byte[] seed = new byte[rand1.length + rand2.length];
        System.arraycopy(rand1, 0, seed, 0, rand1.length);
        System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
        nettyFrontChannel.setSeed(seed);

        // 发送握手数据包
        HandshakePacket hs = new HandshakePacket();
        hs.packetId = 0;
        hs.protocolVersion = Versions.PROTOCOL_VERSION;
        hs.serverVersion = Versions.SERVER_VERSION;
        hs.threadId = nettyFrontChannel.getId();
        hs.seed = rand1;
        hs.serverCapabilities = DdaCapability.getServerCapabilities();
        hs.serverCharsetIndex = (byte) (CharsetUtil.getIndex(SystemConfig.DEFAULT_CHARSET) & 0xff);
        hs.serverStatus = 2;
        hs.restOfScrambleBuff = rand2;

        ctx.writeAndFlush(hs);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
        super.channelUnregistered(ctx);

        //删除连接中的前端对象
        frontChannelTables.remove(remoteAddress);
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

        //删除连接中的前端对象
        frontChannelTables.remove(remoteAddress);
    }

    //=======================================get set method ===================================================
    public static NettyFrontChannel getFrontChannelTables(String remoteAddress) {
        return frontChannelTables.get(remoteAddress);
    }

}
