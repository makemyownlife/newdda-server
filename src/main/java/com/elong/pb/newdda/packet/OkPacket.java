package com.elong.pb.newdda.packet;


import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 * client -- > server
 * 握手后验证
 */
public class OkPacket extends MysqlPacket implements Packet {

    public static final byte FIELD_COUNT = 0x00;

    public static final byte[] OK = new byte[] { 7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0 };

    @Override
    public ByteBuffer encode() {
        return null;
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        return true;
    }

    @Override
    public int calcPacketSize() {
        return 0;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL OK Packet";
    }


}
