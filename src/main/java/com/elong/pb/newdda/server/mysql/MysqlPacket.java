package com.elong.pb.newdda.server.mysql;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/11/23.
 * mysql包抽象类
 */
public abstract class MysqlPacket {

    public int packetLength;

    public byte packetId;


    public abstract ByteBuffer encode();

    /**
     * 计算数据包大小，不包含包头长度。
     */
    public abstract int calcPacketSize();

    /**
     * 取得数据包信息
     */
    protected abstract String getPacketInfo();

    @Override
    public String toString() {
        return new StringBuilder().append(getPacketInfo())
                .append("{length=")
                .append(packetLength)
                .append(",id=")
                .append(packetId)
                .append('}')
                .toString();
    }

}
