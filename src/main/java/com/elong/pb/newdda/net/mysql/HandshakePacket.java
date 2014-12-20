package com.elong.pb.newdda.net.mysql;

import com.elong.pb.newdda.common.BufferUtil;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * From server to client during initial handshake.
 * mysql jdbc 链接 mysql server 服务端发送给客户端 握手包
 */
public class HandshakePacket extends MysqlPacket implements Packet {

    private static final byte[] FILLER_13 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    public byte protocolVersion;

    public byte[] serverVersion;

    public long threadId;

    public byte[] seed;

    public int serverCapabilities;

    public byte serverCharsetIndex;

    public int serverStatus;

    public byte[] restOfScrambleBuff;

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(
               3
               + 1
               + calcPacketSize()
        );
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        buffer.put(protocolVersion);
        BufferUtil.writeWithNull(buffer, serverVersion);
        BufferUtil.writeUB4(buffer, threadId);
        BufferUtil.writeWithNull(buffer, seed);
        BufferUtil.writeUB2(buffer, serverCapabilities);
        buffer.put(serverCharsetIndex);
        BufferUtil.writeUB2(buffer, serverStatus);
        buffer.put(FILLER_13);
        // buffer.position(buffer.position() + 13);
        BufferUtil.writeWithNull(buffer, restOfScrambleBuff);
        buffer.flip();
        return buffer;
    }

    @Override
    public int calcPacketSize() {
        int size = 1;
        size += serverVersion.length;// n
        size += 5;// 1+4
        size += seed.length;// 8
        size += 19;// 1+2+1+2+13
        size += restOfScrambleBuff.length;// 12
        size += 1;// 1
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Handshake Packet";
    }

    //读取byteBuffer 并初始化值
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId =  byteBuffer.get();
        this.protocolVersion = byteBuffer.get();
        this.serverVersion = BufferUtil.readTerminateBytesWithNull(byteBuffer);
        //skip null
        byteBuffer.get();
        this.threadId = BufferUtil.readUB4(byteBuffer);
        this.seed = BufferUtil.readTerminateBytesWithNull(byteBuffer);
        //skip null
        byteBuffer.get();
        this.serverCapabilities = BufferUtil.readUB2(byteBuffer);
        this.serverCharsetIndex = byteBuffer.get();
        this.serverStatus = BufferUtil.readUB2(byteBuffer);
        BufferUtil.stepBuffer(byteBuffer , 13);
        this.restOfScrambleBuff = BufferUtil.readTerminateBytesWithNull(byteBuffer);
        //skip null
        byteBuffer.get();
        return true;
    }

}
