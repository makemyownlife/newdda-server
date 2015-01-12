package com.elong.pb.newdda.net.mysql;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 15/1/12.
 * 与后端的一次会话
 */
public class Session {

    private CountDownLatch latch;

    private String uuid;

    //整个回话过程结束
    private final AtomicBoolean IS_FINISHED = new AtomicBoolean(false);

    private volatile CommandPacket commandPacket;

    public Session(CommandPacket commandPacket) {
        this.latch = new CountDownLatch(1);
        this.commandPacket = commandPacket;
        this.uuid = UUID.randomUUID().toString();
    }

    public final void countDownLatch() {
        if (latch != null) {
            latch.countDown();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CommandPacket getCommandPacket() {
        return commandPacket;
    }

    public void setCommandPacket(CommandPacket commandPacket) {
        this.commandPacket = commandPacket;
    }
    
}
