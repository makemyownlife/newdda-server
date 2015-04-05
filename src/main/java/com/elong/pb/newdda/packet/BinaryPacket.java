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

    public boolean equals(Object binary) {
        if (!(binary instanceof BinaryPacket)) {
            return false;
        }
        BinaryPacket temp = (BinaryPacket) binary;
        byte[] origin = new byte[byteBuffer.remaining()];
        byte[] desti = new byte[temp.byteBuffer.remaining()];
        if (byteBuffer.remaining() != temp.byteBuffer.remaining()) {
            return false;
        }
        byteBuffer.get(origin);
        temp.byteBuffer.get(desti);
        for (int i = 0; i < origin.length; i++) {
            if (origin[i] != desti[i]) {
                return false;
            }
        }
        byteBuffer.flip();
        temp.byteBuffer.flip();
        return true;
    }

}
