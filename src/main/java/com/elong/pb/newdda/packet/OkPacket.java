package com.elong.pb.newdda.packet;


import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 * client -- > server
 * 握手后验证
 */
public class OkPacket extends MysqlPacket implements Packet {

    public static final byte FIELD_COUNT = 0x00;

    public static final byte[] OK = new byte[]{7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0};

    public byte fieldCount = FIELD_COUNT;

    public long affectedRows;

    public long insertId;

    public int serverStatus;

    public int warningCount;

    public byte[] message;

    @Override
    public ByteBuffer encode() {
        int length = 3 + 1 + calcPacketSize();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        buffer.put(fieldCount);
        BufferUtil.writeLength(buffer, affectedRows);
        BufferUtil.writeLength(buffer, insertId);
        BufferUtil.writeUB2(buffer, serverStatus);
        BufferUtil.writeUB2(buffer, warningCount);
        if (message != null) {
            BufferUtil.writeWithLength(buffer, message);
        }
        buffer.flip();
        return buffer;
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        packetLength = BufferUtil.readUB3(byteBuffer);
        packetId = byteBuffer.get();
        fieldCount = byteBuffer.get();
        affectedRows = BufferUtil.readLength(byteBuffer);
        insertId = BufferUtil.readLength(byteBuffer);
        serverStatus = BufferUtil.readUB2(byteBuffer);
        warningCount = BufferUtil.readUB2(byteBuffer);
        if (byteBuffer.hasRemaining()) {
            this.message = BufferUtil.readBytesWithLength(byteBuffer);
        }
        return true;
    }

    @Override
    public int calcPacketSize() {
        int i = 1;
        i += BufferUtil.getLength(affectedRows);
        i += BufferUtil.getLength(insertId);
        i += 4;
        if (message != null) {
            i += BufferUtil.getLength(message);
        }
        return i;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL OK Packet";
    }


}
