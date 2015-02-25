package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.common.CharsetUtil;
import com.elong.pb.newdda.common.SecurityUtil;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.config.DdaCapability;
import com.elong.pb.newdda.config.NettyClientConfig;
import com.elong.pb.newdda.exception.UnknownPacketException;
import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.packet.AuthPacket;
import com.elong.pb.newdda.packet.ErrorPacket;
import com.elong.pb.newdda.packet.HandshakePacket;
import com.elong.pb.newdda.packet.OkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangyong on 15/2/13.
 * 后端链接验证 ,验证成功后才可以放到 可用连接池中
 */
public class BackendAuthHandler implements Handler {

    private final static Logger logger = LoggerFactory.getLogger(BackendAuthHandler.class);

    private CountDownLatch countDownLatch;

    private BackendDdaChannel backendDdaChannel;

    private volatile boolean authPacketSend;

    public BackendAuthHandler(BackendDdaChannel backendDdaChannel) {
        this.backendDdaChannel = backendDdaChannel;
        this.countDownLatch = new CountDownLatch(1);
        this.authPacketSend = false;
    }

    public boolean isAuthPacketSend() {
        return authPacketSend;
    }

    public void setAuthPacketSend(boolean authPacketSend) {
        this.authPacketSend = authPacketSend;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    @Override
    public void handle(ByteBuffer byteBuffer) throws Exception {
        //是否发送了auth命令 ，若没有发auth命令 ，则需要验证handshake包
        if (!isAuthPacketSend()) {
            logger.info("后端链接:{}", this.backendDdaChannel + " 处理握手包");
            HandshakePacket handshakePacket = new HandshakePacket();
            handshakePacket.decode(byteBuffer);
            //发送auth packet
            authMysqlChannel(handshakePacket);
            this.authPacketSend = true;
        } else {
            logger.info("后端链接:{}", this.backendDdaChannel + " 处理验证返回包");
            authResponsePacket(byteBuffer);
        }
    }

    private void authMysqlChannel(HandshakePacket handshakePacket) throws Exception {
        int charsetIndex = handshakePacket.serverCharsetIndex & 0xff;
        //设置charset
        String charset = null;
        if ((charset = CharsetUtil.getCharset(charsetIndex)) != null) {
            this.backendDdaChannel.setCharset(charset);
        }
        AuthPacket authPacket = new AuthPacket();
        authPacket.packetId = 1;
        authPacket.clientFlags = DdaCapability.getClientFlags();
        authPacket.maxPacketSize = NettyClientConfig.MAX_PACKET_SIZE;
        authPacket.charsetIndex = handshakePacket.serverCharsetIndex & 0xff;
        //查询出相关的链接信息
        DataSourceLocation dataSourceLocation = this.backendDdaChannel.getBackendChannelPool().getDataSourceLocation();
        authPacket.user = dataSourceLocation.getUser();
        String passwd = dataSourceLocation.getPassword();
        String database = dataSourceLocation.getDatabaseName();
        if (passwd != null && passwd.length() > 0) {
            byte[] password = passwd.getBytes(this.backendDdaChannel.getCharset());
            byte[] seed = handshakePacket.seed;
            byte[] restOfScramble = handshakePacket.restOfScrambleBuff;
            byte[] authSeed = new byte[seed.length + restOfScramble.length];
            System.arraycopy(seed, 0, authSeed, 0, seed.length);
            System.arraycopy(restOfScramble, 0, authSeed, seed.length, restOfScramble.length);
            authPacket.password = SecurityUtil.scramble411(password, authSeed);
        }
        authPacket.database = database;
        this.backendDdaChannel.write(authPacket);
    }

    private void authResponsePacket(ByteBuffer byteBuffer) {
        switch (byteBuffer.get(4)) {
            case OkPacket.FIELD_COUNT:
                logger.info("后端链接:{}", this.backendDdaChannel + " 成功握手.");
                this.countDownLatch.countDown();
                this.backendDdaChannel.setAuthenticated(true);
                break;
            case ErrorPacket.FIELD_COUNT:
                logger.info("后端链接:{}", this.backendDdaChannel + " 握手失败,可能包有问题");
                this.countDownLatch.countDown();
                break;
            default:
                this.countDownLatch.countDown();
                throw new UnknownPacketException("sorry Can't find auth response!!");
        }
    }

}
