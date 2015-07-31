package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.CharsetUtil;
import com.elong.pb.newdda.common.RandomUtil;
import com.elong.pb.newdda.common.netty.ChannelEventListener;
import com.elong.pb.newdda.config.DdaCapability;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.config.Versions;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.packet.HandshakePacket;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端链接事件监听器
 * Created by zhangyong on 15/7/23.
 */
public class FrontEventListener implements ChannelEventListener {

    private final static Logger logger = LoggerFactory.getLogger(FrontEventListener.class);

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

        //在前端注册链接信息 因为是服务端先发握手包
        FrontDdaChannel frontDdaChannel = new FrontDdaChannel(channel);
        FrontClient.getInstance().addFrontDdaChannel(frontDdaChannel);

        // 生成认证数据
        byte[] rand1 = RandomUtil.randomBytes(8);
        byte[] rand2 = RandomUtil.randomBytes(12);

        //保存认证数据
        byte[] seed = new byte[rand1.length + rand2.length];
        System.arraycopy(rand1, 0, seed, 0, rand1.length);
        System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
        frontDdaChannel.setSeed(seed);

        //为前端发送握手包
        HandshakePacket hs = new HandshakePacket();
        hs.packetId = 0;
        hs.setProtocolVersion(Versions.PROTOCOL_VERSION);
        hs.setServerVersion(Versions.SERVER_VERSION);
        hs.setThreadId(frontDdaChannel.getId());
        hs.setSeed(rand1);
        hs.setServerCapabilities(DdaCapability.getServerCapabilities());
        hs.setServerCharsetIndex((byte)(CharsetUtil.getIndex(SystemConfig.DEFAULT_CHARSET) & 0xff));
        hs.setServerStatus(2);
        hs.setRestOfScrambleBuff(rand2);

        frontDdaChannel.write(hs);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {

    }

}
