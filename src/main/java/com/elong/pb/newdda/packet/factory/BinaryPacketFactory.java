package com.elong.pb.newdda.packet.factory;

import com.elong.pb.newdda.packet.BinaryPacket;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/29.
 * 命令包的创建
 */
public class BinaryPacketFactory {

    public static BinaryPacket createBinaryHeapByteBuffer(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        int limit = byteBuffer.limit();
        ByteBuffer newheapByteBuffer = ByteBuffer.allocate(limit);
        newheapByteBuffer.put(byteBuffer);
        newheapByteBuffer.flip();
        BinaryPacket binaryPacket = new BinaryPacket(newheapByteBuffer);
        return binaryPacket;
    }

}
