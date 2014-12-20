package com.elong.pb.newdda.net.handler.backend;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.HandshakePacket;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.server.NettyBackendChannel;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public MysqlPacket handle(ByteBuf byteBuf) {
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

        ByteBuf data = byteBuf.slice(byteBuf.readerIndex(), length + 1 + 3);
        ByteBuffer byteBuffer = data.nioBuffer();

        HandshakePacket handshakePacket = new HandshakePacket();
        handshakePacket.decode(byteBuffer);

        return handshakePacket;
    }

    @Override
    public Packet handle(MysqlPacket mysqlPacket) {
        return null;
    }

}
