package com.elong.pb.newdda.server;

import com.elong.pb.newdda.common.CharsetUtil;
import com.elong.pb.newdda.common.RandomUtil;
import com.elong.pb.newdda.common.netty.ChannelEventListener;
import com.elong.pb.newdda.config.DdaCapability;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.config.Versions;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.HandshakePacket;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/2/13.
 */
public class FrontEventListener implements ChannelEventListener {

    private final static Logger logger = LoggerFactory.getLogger(FrontEventListener.class);

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
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

        // 发送握手数据包
        HandshakePacket hs = new HandshakePacket();
        hs.packetId = 0;
        hs.protocolVersion = Versions.PROTOCOL_VERSION;
        hs.serverVersion = Versions.SERVER_VERSION;
        hs.threadId = frontDdaChannel.getId();
        hs.seed = rand1;
        hs.serverCapabilities = DdaCapability.getServerCapabilities();
        hs.serverCharsetIndex = (byte) (CharsetUtil.getIndex(SystemConfig.DEFAULT_CHARSET) & 0xff);
        hs.serverStatus = 2;
        hs.restOfScrambleBuff = rand2;

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
