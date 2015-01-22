package com.elong.pb.newdda.net.mysql;

/**
 * Created by zhangyong on 15/1/22.
 */
public class SetSession extends Session {

    @Override
    public boolean decode(MysqlPacket mysqlPacket) {
        return false;
    }

}
