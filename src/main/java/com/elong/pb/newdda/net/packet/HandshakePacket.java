package com.elong.pb.newdda.net.packet;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * 前端创建链接后
 * * From server to client during initial handshake.
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1                            protocol_version
 * n (Null-Terminated String)   server_version
 * 4                            thread_id
 * 8                            scramble_buff
 * 1                            (filler) always 0x00
 * 2                            server_capabilities
 * 1                            server_language
 * 2                            server_status
 * 13                           (filler) always 0x00 ...
 * 13                           rest of scramble_buff (4.1)
 *
 * @see please look at following url:
 * http://dev.mysql.com/doc/internals/en/client-server-protocol.html
 * </pre>
 * Created by zhangyong on 15/7/26.
 */
public class HandshakePacket extends MysqlPacket implements Packet {

    private static final byte[] FILLER_13 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private byte protocolVersion;

    private byte[] serverVersion;

    private long threadId;

    private byte[] seed;

    private int serverCapabilities;

    private byte serverCharsetIndex;

    private int serverStatus;

    private byte[] restOfScrambleBuff;

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
        BufferUtil.stepBuffer(byteBuffer, 13);
        this.restOfScrambleBuff = BufferUtil.readTerminateBytesWithNull(byteBuffer);
        //skip null
        byteBuffer.get();
        return true;
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

    //======================================set get method ====================================================
    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public byte[] getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(byte[] serverVersion) {
        this.serverVersion = serverVersion;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public int getServerCapabilities() {
        return serverCapabilities;
    }

    public void setServerCapabilities(int serverCapabilities) {
        this.serverCapabilities = serverCapabilities;
    }

    public byte getServerCharsetIndex() {
        return serverCharsetIndex;
    }

    public void setServerCharsetIndex(byte serverCharsetIndex) {
        this.serverCharsetIndex = serverCharsetIndex;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(int serverStatus) {
        this.serverStatus = serverStatus;
    }

    public byte[] getRestOfScrambleBuff() {
        return restOfScrambleBuff;
    }

    public void setRestOfScrambleBuff(byte[] restOfScrambleBuff) {
        this.restOfScrambleBuff = restOfScrambleBuff;
    }

}
