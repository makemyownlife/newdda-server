package com.elong.pb.newdda.net.handler.backend;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.*;
import com.elong.pb.newdda.server.NettyBackendChannel;
import com.elong.pb.newdda.server.NettySessionExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/19.
 * 后端验证操作
 */
public class BackendCommandHandler implements NettyHandler {

    private static Logger logger = LoggerFactory.getLogger(BackendCommandHandler.class);

    private NettyBackendChannel nettyBackendChannel;

    public BackendCommandHandler(NettyBackendChannel nettyBackendChannel) {
        this.nettyBackendChannel = nettyBackendChannel;
    }

    @Override
    public MysqlPacket handle(ChannelHandlerContext ctx, ByteBuf byteBuf) {
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

        ByteBuf frame = ctx.alloc().buffer(length + 1 + 3);
        frame.writeBytes(byteBuf, byteBuf.readerIndex(), length);
        ByteBuffer byteBuffer = frame.nioBuffer();

        BinaryPacket binaryPacket = new BinaryPacket(byteBuffer);
        byteBuf.skipBytes(length + 1 + 3);
        return binaryPacket;
    }

    @Override
    public Packet handle(MysqlPacket mysqlPacket) {
        if ((mysqlPacket instanceof CommandPacket)) {
            CommandPacket commandPacket = (CommandPacket) mysqlPacket;
            //分析
            switch (commandPacket.command) {
                case OkPacket.COM_QUERY:
                    String sql = null;
                    try {
                        sql = new String(commandPacket.arg, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    logger.info("sql==" + sql);
                    break;
            }
            return commandPacket;
        } else {
            Session session = NettySessionExecutor.SESSION_REFERENCE.get();
            session.decode(mysqlPacket);
        }
        return null;
    }

}
