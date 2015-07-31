package com.elong.pb.newdda.net.packet;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/2/11.
 * 基本的包 可以编码 发送到对方
 */
public interface Packet {

    public ByteBuffer encode();

}
