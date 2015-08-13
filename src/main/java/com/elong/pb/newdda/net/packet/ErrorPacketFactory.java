package com.elong.pb.newdda.net.packet;

import java.io.UnsupportedEncodingException;

/**
 * 创建错误的包
 * User: zhangyong
 * Date: 2015/3/11
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public class ErrorPacketFactory {

    private final static byte[] encodeString(String src, String charset) {
        if (src == null) {
            return null;
        }
        if (charset == null) {
            return src.getBytes();
        }
        try {
            return src.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return src.getBytes();
        }
    }

    public static ErrorPacket newInstance(byte id, int errno, String msg) {
        ErrorPacket errorPacket = new ErrorPacket();
        errorPacket.packetId = id;
        errorPacket.setErrno (errno);
        errorPacket.setMsg(msg);
        return errorPacket;
    }

    public static ErrorPacket errorMessage(int errno, String msg) {
        ErrorPacket errorPacket = new ErrorPacket();
        errorPacket.packetId = (byte) 1;
        errorPacket.setErrno (errno);
        errorPacket.setMsg(msg);
        return errorPacket;
    }

}
