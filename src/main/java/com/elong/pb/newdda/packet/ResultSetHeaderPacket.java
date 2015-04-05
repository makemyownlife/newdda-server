package com.elong.pb.newdda.packet;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/1/13.
 * result set 头部 header
 */
public class ResultSetHeaderPacket extends MysqlPacket implements Packet {

    public int fieldCount;

    public long extra;

    @Override
    public int calcPacketSize() {
        int size = BufferUtil.getLength(fieldCount);
        if (extra > 0) {
            size += BufferUtil.getLength(extra);
        }
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL ResultSetHeader Packet";
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId = byteBuffer.get();
        this.fieldCount = (int) BufferUtil.readLength(byteBuffer);
        if (byteBuffer.hasRemaining()) {
            this.extra = BufferUtil.readLength(byteBuffer);
        }
        return true;
    }

    @Override
    public ByteBuffer encode() {
        int packetSize = calcPacketSize();
        ByteBuffer buffer = ByteBuffer.allocate(
                3 + 1 + packetSize

        );
        BufferUtil.writeUB3(buffer, packetSize);
        buffer.put(packetId);
        BufferUtil.writeLength(buffer, fieldCount);
        if (extra > 0) {
            BufferUtil.writeLength(buffer, extra);
        }
        buffer.flip();
        return buffer;
    }

}
