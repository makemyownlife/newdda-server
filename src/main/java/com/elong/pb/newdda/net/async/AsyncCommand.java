package com.elong.pb.newdda.net.async;

import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.MysqlPacket;

/**
 * Created by zhangyong on 15/4/4.
 * 基本的查询
 */
public interface AsyncCommand {

    public void asyncMysqlPacket(BackendDdaChannel backendDdaChannel, MysqlPacket mysqlPacket);

    public void encodeForFront(FrontDdaChannel frontDdaChannel);

}
