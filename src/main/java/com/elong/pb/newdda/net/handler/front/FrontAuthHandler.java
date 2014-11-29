package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangyong on 14/11/28.
 * 前端验证处理器
 */
public class FrontAuthHandler implements NettyHandler {

    private NettyFrontChannel nettyFrontChannel;

    public FrontAuthHandler(NettyFrontChannel nettyFrontChannel){
        this.nettyFrontChannel = nettyFrontChannel;
    }

    @Override
    public void handle(ByteBuf byteBuf) {

    }

}
