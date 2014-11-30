package com.elong.pb.newdda.net.mysql;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * 基础的包
 * User: zhangyong
 * Date: 2014/11/30
 * Time: 16:45
 * if you have any question ,please contact zhangyong7120180@163.com
 */
public interface Packet {

    public ByteBuffer encode();

}
