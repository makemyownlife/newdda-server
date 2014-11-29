package com.elong.pb.newdda.net.handler;

import io.netty.buffer.ByteBuf;

/**
 * Created by zhangyong on 14/11/28.
 */
public interface NettyHandler {

    public void handle(ByteBuf byteBuf);

}
