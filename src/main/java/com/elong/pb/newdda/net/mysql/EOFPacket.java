package com.elong.pb.newdda.net.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/1/17.
 *
 */
public class EOFPacket  extends MysqlPacket implements Packet{

    public static final byte FIELD_COUNT = (byte) 0xfe;

    @Override
    public int calcPacketSize() {
        return 0;
    }

    @Override
    public String getPacketInfo() {
        return null;
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public ByteBuffer encode() {
        return null;
    }

}
