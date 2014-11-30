package com.elong.pb.newdda.net.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 * client -- > server
 * 握手后验证
 */
public class AuthPacket extends MysqlPacket implements Packet {

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
