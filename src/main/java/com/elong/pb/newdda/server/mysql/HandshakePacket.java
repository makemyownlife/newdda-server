package com.elong.pb.newdda.server.mysql;

/**
 * Created by zhangyong on 14/11/23.
 * From server to client during initial handshake.
 * mysql jdbc 链接 mysql server 服务端发送给客户端 握手包
 *
 */
public class HandshakePacket extends MysqlPacket{

    @Override
    public int calcPacketSize() {
        return 0;
    }

    @Override
    protected String getPacketInfo() {
        return null;
    }

}
