package com.elong.pb.newdda.net.mysql;

import com.elong.pb.newdda.server.NettyFrontConnetManageHandler;
import com.elong.pb.newdda.server.NettySessionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/1/22.
 */
public class SetSession extends Session {

    private static Logger logger = LoggerFactory.getLogger(SetSession.class);

    @Override
    public boolean decode(MysqlPacket mysqlPacket) {
        logger.info("setSession decode..");
        BinaryPacket temp = (BinaryPacket) mysqlPacket;
        temp.setReturn(true);
        NettyFrontConnetManageHandler.getNettyFrontChannel().getChannel()
                .writeAndFlush(temp);
        countDownLatch();
        NettySessionExecutor.SESSION_REFERENCE.set(null);
        return true;
    }

}
