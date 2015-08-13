package com.elong.pb.newdda.net.packet;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/29.
 * 命令mysql packet
 */
public class CommandPacket extends MysqlPacket implements Packet {

    public byte command;

    public byte[] arg;

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId = byteBuffer.get();
        this.command = byteBuffer.get();
        this.arg = new byte[this.packetLength - 1];
        byteBuffer.get(arg);
        return true;
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(
                3 + 1 + calcPacketSize()
        );
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        buffer.put(command);
        buffer.put(arg);
        buffer.flip();
        return buffer;
    }

    @Override
    public int calcPacketSize() {
        return 1 + arg.length;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Command Packet";
    }

}
