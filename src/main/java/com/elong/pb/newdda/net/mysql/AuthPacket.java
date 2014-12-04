package com.elong.pb.newdda.net.mysql;


import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.common.ByteUtil;
import com.elong.pb.newdda.config.Capabilities;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 * client -- > server
 * 握手后验证
 */
public class AuthPacket extends MysqlPacket implements Packet {

    private static final byte[] FILLER = new byte[23];

    public long clientFlags;

    public long maxPacketSize;

    public int charsetIndex;

    public byte[] extra;// from FILLER(23)

    public String user;

    public byte[] password;

    public String database;

    @Override
    public ByteBuffer encode() {
        return null;
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId = byteBuffer.get();
        this.clientFlags = BufferUtil.readUB4(byteBuffer);
        this.maxPacketSize = BufferUtil.readUB4(byteBuffer);
        this.charsetIndex = (byteBuffer.get() & 0xff);

        int current = byteBuffer.position();
        //read extra
        int len = (int)BufferUtil.readLength(byteBuffer);
        if(len > 0 && len < FILLER.length){
            byte[] ab = new byte[len];
            byteBuffer.get(ab, current, len);
        }

        byteBuffer.position(current + FILLER.length);
        this.user = BufferUtil.readTerminateStringWithNull(byteBuffer);

        //因为最后一个是 byte 0 所以必须skip掉
        byteBuffer.get();
        this.password = BufferUtil.readBytesWithLength(byteBuffer);

        if (((clientFlags & Capabilities.CLIENT_CONNECT_WITH_DB) != 0) && byteBuffer.hasRemaining()) {
            database = BufferUtil.readTerminateStringWithNull(byteBuffer);
        }

        return true;
    }

    @Override
    public int calcPacketSize() {
        return 0;
    }

    public long getClientFlags() {
        return clientFlags;
    }

    public void setClientFlags(long clientFlags) {
        this.clientFlags = clientFlags;
    }

    public long getMaxPacketSize() {
        return maxPacketSize;
    }

    public void setMaxPacketSize(long maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    public int getCharsetIndex() {
        return charsetIndex;
    }

    public void setCharsetIndex(int charsetIndex) {
        this.charsetIndex = charsetIndex;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Authentication Packet";
    }



}
