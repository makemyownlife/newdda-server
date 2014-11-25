package com.elong.pb.newdda.server.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * From server to client during initial handshake.
 * mysql jdbc 链接 mysql server 服务端发送给客户端 握手包
 *
 */
public class HandshakePacket extends MysqlPacket{

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
        return null;
    }

    @Override
    public int calcPacketSize() {
        return 0;
    }

    @Override
    protected String getPacketInfo() {
        return null;
    }

}
