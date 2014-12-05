package com.elong.pb.newdda.net.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/6.
 * 简单的数据包
 */
public class SimplePacket implements Packet {

    private ByteBuffer byteBuffer;

    public SimplePacket(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public ByteBuffer encode() {
        return byteBuffer;
    }

}
