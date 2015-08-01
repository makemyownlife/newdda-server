package com.elong.pb.newdda.net.packet;

import java.nio.ByteBuffer;

/**
 * 二进制的mysql包
 * Created by zhangyong on 15/7/31.
 */
public class BinaryMySqlPacket extends MysqlPacket implements Packet {

    private ByteBuffer byteBuffer;

    private boolean isDirect;

    public BinaryMySqlPacket(ByteBuffer heapByteBuffer, boolean isDirect) {
        this.byteBuffer = heapByteBuffer;
        this.isDirect = isDirect;
    }

    @Override
    public int calcPacketSize() {
        //在二进制包里这里仅仅是一个数值没有什么用处 仅仅是一个标志而已 不用管
        return this.byteBuffer == null ? 0 : this.byteBuffer.limit();
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Binary Packet";
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public ByteBuffer encode() {
        return this.byteBuffer;
    }

    //=========================================== get set method ================================================
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean isDirect) {
        this.isDirect = isDirect;
    }

}
