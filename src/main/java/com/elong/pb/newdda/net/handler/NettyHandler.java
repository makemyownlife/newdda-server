package com.elong.pb.newdda.net.handler;

import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangyong on 14/11/28.
 */
public interface NettyHandler {

    //decode 成 MysqlPacket
    public MysqlPacket handle(ByteBuf byteBuf);

    // MysqlPacket 转 packet
    public Packet handle(MysqlPacket mysqlPacket);

}
