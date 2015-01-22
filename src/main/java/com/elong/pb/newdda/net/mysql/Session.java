package com.elong.pb.newdda.net.mysql;

import com.elong.pb.newdda.server.NettyFrontConnetManageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 15/1/12.
 * 与后端的一次会话
 */
public abstract class Session {

    private Packet result;

    public Packet getResult() {
        return result;
    }

    private CountDownLatch latch;

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public abstract boolean decode(MysqlPacket mysqlPacket);

}
