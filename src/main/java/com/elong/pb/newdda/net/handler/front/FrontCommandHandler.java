package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/12/6.
 * 命令相关
 */
public class FrontCommandHandler implements NettyHandler {

    private static Logger logger  = LoggerFactory.getLogger(FrontCommandHandler.class);

    private NettyFrontChannel nettyFrontChannel;

    public FrontCommandHandler(NettyFrontChannel nettyFrontChannel){
        this.nettyFrontChannel = nettyFrontChannel;
    }

    @Override
    public MysqlPacket handle(ByteBuf byteBuf) {
        return null;
    }

    @Override
    public Packet handle(MysqlPacket mysqlPacket) {
        return null;
    }

}
