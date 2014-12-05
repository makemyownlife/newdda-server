package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.AuthPacket;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.net.mysql.SimplePacket;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/28.
 * 前端验证处理器
 */
public class FrontAuthHandler implements NettyHandler {

    private static Logger logger = LoggerFactory.getLogger(FrontAuthHandler.class);

    private static final byte[] AUTH_OK = new byte[]{7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0};

    private NettyFrontChannel nettyFrontChannel;

    public FrontAuthHandler(NettyFrontChannel nettyFrontChannel) {
        this.nettyFrontChannel = nettyFrontChannel;
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

        AuthPacket authPacket = new AuthPacket();
        //添加验证成功
        boolean flag = authPacket.decode(byteBuffer);

        //过滤掉相关的字节 使读索引跳到相关的索引
        byteBuf.skipBytes(length + 1 + 3);
        return authPacket;
    }

    @Override
    public Packet handle(MysqlPacket mysqlPacket) {
        AuthPacket authPacket = (AuthPacket) mysqlPacket;



        return success(authPacket);
    }

    protected SimplePacket success(AuthPacket auth) {
        nettyFrontChannel.setAuthenticated(true);
        nettyFrontChannel.setNettyHandler(new FrontCommandHandler(nettyFrontChannel));
        if (logger.isInfoEnabled()) {
            StringBuilder s = new StringBuilder();
            s.append(RemotingHelper.parseChannelRemoteAddr(nettyFrontChannel.getChannel())).append('\'').append(auth.user).append("' login success");
            byte[] extra = auth.extra;
            if (extra != null && extra.length > 0) {
                s.append(",extra:").append(new String(extra));
            }
            logger.info(s.toString());
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(AUTH_OK.length);
        byteBuffer.put(AUTH_OK);
        SimplePacket simplePacket = new SimplePacket(byteBuffer);
        return simplePacket;
    }

}