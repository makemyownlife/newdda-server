package com.elong.pb.newdda.net.async;

import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.MysqlPacket;

/**
 * Created by zhangyong on 15/4/4.
 */
public class UpdateAsyncCommand implements AsyncCommand {

    private long affectedRows;

    private long insertId;

    @Override
    public void asyncMysqlPacket(BackendDdaChannel backendDdaChannel, MysqlPacket mysqlPacket) {

    }

    @Override
    public void encodeForFront(FrontDdaChannel frontDdaChannel) {

    }

}
