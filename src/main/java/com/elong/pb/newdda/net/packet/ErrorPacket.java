package com.elong.pb.newdda.net.packet;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * 返回给client错误的包
 * Created by zhangyong on 15/8/11.
 */
public class ErrorPacket extends MysqlPacket implements Packet  {

    public static final byte FIELD_COUNT = (byte) 0xff;

    private static final byte SQLSTATE_MARKER = (byte) '#';

    private static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();

    public byte fieldCount = FIELD_COUNT;

    private int errno;

    private String msg;

    private byte mark = SQLSTATE_MARKER;

    private byte[] sqlState = DEFAULT_SQLSTATE;

    @Override
    public int calcPacketSize() {
        int size = 9;// 1 + 2 + 1 + 5
        if (msg != null) {
            size += msg.getBytes().length;
        }
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Error Packet";
    }

    @Deprecated
    public boolean decode(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(
                3
                        + 1
                        + calcPacketSize()
        );
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        buffer.put(fieldCount);
        BufferUtil.writeUB2(buffer, errno);
        buffer.put(mark);
        buffer.put(sqlState);
        if (msg != null) {
            buffer.put(msg.getBytes());
        }
        buffer.flip();
        return buffer;
    }

    //=====================================set  get method ======================================================
    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
