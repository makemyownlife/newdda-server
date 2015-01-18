package com.elong.pb.newdda.net.handler.backend;

import com.elong.pb.newdda.common.CharsetUtil;
import com.elong.pb.newdda.common.SecurityUtil;
import com.elong.pb.newdda.config.DdaCapability;
import com.elong.pb.newdda.config.NettyClientConfig;
import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.*;
import com.elong.pb.newdda.server.NettyBackendChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/19.
 * 后端验证操作
 */
public class BackendAuthHandler implements NettyHandler {

    private static Logger logger = LoggerFactory.getLogger(BackendAuthHandler.class);

    private NettyBackendChannel nettyBackendChannel;

    public BackendAuthHandler(NettyBackendChannel nettyBackendChannel) {
        this.nettyBackendChannel = nettyBackendChannel;
    }

    @Override
    public MysqlPacket handle(ChannelHandlerContext ctx , ByteBuf byteBuf) throws IOException {
        if (byteBuf == null || byteBuf.readableBytes() == 0) {
            return null;
        }
        int readableLength = byteBuf.readableBytes();
        //前3个字节表示
        if (readableLength < 3) {
            return null;
        }
        byteBuf.markReaderIndex();
        int readerIndex = byteBuf.readerIndex();

        //读取packet的长度 前3个字节
        int length = byteBuf.getByte(readerIndex) & 0xff;
        length |= (byteBuf.getByte(++readerIndex) & 0xff) << 8;
        length |= (byteBuf.getByte(++readerIndex) & 0xff) << 16;

        //数据包不够解析直接返回
        if (readableLength < length + 3 + 1) {
            byteBuf.resetReaderIndex();
            return null;
        }
        byteBuf.resetReaderIndex();

        int totalLength = length + 1 + 3;
        ByteBuf frame = ctx.alloc().buffer(length + 1 + 3);
        frame.writeBytes(byteBuf, byteBuf.readerIndex(), totalLength);
        ByteBuffer byteBuffer = frame.nioBuffer();

        MysqlPacket mysqlPacket = null;
        if (!nettyBackendChannel.isSendAuth) {
            mysqlPacket = new HandshakePacket();
            mysqlPacket.decode(byteBuffer);
            //过滤掉相关的字节 使读索引跳到相关的索引 需要加相关代码 其实 是挺 silly的事情 下一版的dda必须修改这条
            byteBuf.skipBytes(totalLength);
        } else {
            switch (byteBuffer.get(4)) {
                case OkPacket.FIELD_COUNT:
                    logger.info("已经发送了Auth,执行auth ok操作");
                    nettyBackendChannel.setAuthenticated(true);
                    //设置处理handler类
                    nettyBackendChannel.setNettyHandler(new BackendCommandHandler(nettyBackendChannel));
                    byteBuf.skipBytes(length + 1 + 3);
                    break;
                default:
                    logger.error("返回参数错误");
            }
        }
        return mysqlPacket;
    }

    @Override
    //将握手包转成 （411验证包）
    public Packet handle(MysqlPacket mysqlPacket) throws IOException{
        HandshakePacket handshakePacket = (HandshakePacket) mysqlPacket;
        nettyBackendChannel.setThreadId(handshakePacket.threadId);
        //设置字符集
        int charsetIndex = handshakePacket.serverCharsetIndex & 0xff;
        String charset = null;
        if ((charset = CharsetUtil.getCharset(charsetIndex)) != null) {
            nettyBackendChannel.setCharset(charset);
        }
        try {
            AuthPacket authPacket = sendAuthPacketToMySqlServer(handshakePacket);
            return authPacket;
        } finally {
            nettyBackendChannel.isSendAuth = true;
        }
    }

    private AuthPacket sendAuthPacketToMySqlServer(HandshakePacket handshakePacket) {
        try {
            AuthPacket authPacket = new AuthPacket();
            authPacket.packetId = 1;
            authPacket.clientFlags = DdaCapability.getClientFlags();
            authPacket.maxPacketSize = NettyClientConfig.MAX_PACKET_SIZE;
            authPacket.charsetIndex = handshakePacket.serverCharsetIndex & 0xff;
            //默认用户名 以及密码先写死
            authPacket.user = "root";
            String passwd = "ilxw";
            String database = "blog";
            if (passwd != null && passwd.length() > 0) {
                byte[] password = passwd.getBytes(nettyBackendChannel.getCharset());
                byte[] seed = handshakePacket.seed;
                byte[] restOfScramble = handshakePacket.restOfScrambleBuff;
                byte[] authSeed = new byte[seed.length + restOfScramble.length];
                System.arraycopy(seed, 0, authSeed, 0, seed.length);
                System.arraycopy(restOfScramble, 0, authSeed, seed.length, restOfScramble.length);
                authPacket.password = SecurityUtil.scramble411(password, authSeed);
            }
            authPacket.database = database;
            return authPacket;
        } catch (Exception e) {
            logger.error("sendAuthPacketToMySqlServer error :", e);
        }
        return null;
    }


}
