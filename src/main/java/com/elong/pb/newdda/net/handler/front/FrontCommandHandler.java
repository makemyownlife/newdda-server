package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/6.
 * 命令相关
 */
public class FrontCommandHandler implements NettyHandler {

    private static Logger logger = LoggerFactory.getLogger(FrontCommandHandler.class);

    private NettyFrontChannel nettyFrontChannel;

    public FrontCommandHandler(NettyFrontChannel nettyFrontChannel) {
        this.nettyFrontChannel = nettyFrontChannel;
    }

    @Override
    public MysqlPacket handle(ByteBuf byteBuf) {
        if (byteBuf != null && byteBuf.readableBytes() == 0) {
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

        //完整的数据包
        ByteBuf data = byteBuf.slice(byteBuf.readerIndex(), length + 1 + 3);
        ByteBuffer byteBuffer = data.nioBuffer();

        switch (byteBuffer.get(4)) {
            case MysqlPacket.COM_INIT_DB :

        }
        return null;
    }

    @Override
    public Packet handle(MysqlPacket mysqlPacket) {
        return null;
    }

}
