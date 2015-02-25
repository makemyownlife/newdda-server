package com.elong.pb.newdda.packet;

import java.nio.ByteBuffer;

/**
 * 抽象的二进制包
 * User: zhangyong
 * Date: 2014/12/8
 * Time: 22:37
 * if you have any question ,please contact zhangyong7120180@163.com
 */
public class BinaryPacket extends MysqlPacket implements Packet {

    private ByteBuffer byteBuffer;

    public BinaryPacket(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int calcPacketSize() {
        return this.byteBuffer == null ? 0 : this.byteBuffer.limit();
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Binary Packet";
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public ByteBuffer encode() {
        return this.byteBuffer;
    }

}
