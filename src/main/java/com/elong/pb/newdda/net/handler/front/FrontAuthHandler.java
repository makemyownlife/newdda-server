package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangyong on 14/11/28.
 * 前端验证处理器
 */
public class FrontAuthHandler implements NettyHandler {

    private static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0 };

    private NettyFrontChannel nettyFrontChannel;

    public FrontAuthHandler(NettyFrontChannel nettyFrontChannel){
        this.nettyFrontChannel = nettyFrontChannel;
    }

    @Override
    public MysqlPacket handle(ByteBuf byteBuf) {
        return null;
    }

}
