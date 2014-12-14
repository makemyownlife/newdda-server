package com.elong.pb.newdda.net.mysql;

/**
 * Created by zhangyong on 14/12/14.
 * 创建error packet
 *
 */
public class ErrorPacketFactory {

    public static ErrorPacket createErrorPacket(byte id, int errno, String msg) {
        ErrorPacket err = new ErrorPacket();
        err.packetId = id;
        err.errno = errno;
        err.msg =  msg;
        return err;
    }



}
