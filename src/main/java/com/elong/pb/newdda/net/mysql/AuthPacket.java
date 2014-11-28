package com.elong.pb.newdda.net.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 * client -- > server
 * 握手后验证
 *
 */
public class AuthPacket extends MysqlPacket {

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
