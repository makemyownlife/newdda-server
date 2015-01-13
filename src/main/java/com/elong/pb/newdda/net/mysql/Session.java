package com.elong.pb.newdda.net.mysql;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 15/1/12.
 * 与后端的一次会话
 */
public class Session {

    public static enum ParseStatus {
        NULL,HEADER, FIELD, FIELD_EOF, ROWDATA, LAST_EOF
    }

    private CountDownLatch latch;

    private String uuid;

    private volatile ParseStatus parseStatus = null;

    //整个回话过程结束
    private final AtomicBoolean IS_FINISHED = new AtomicBoolean(false);

    private Packet result;

    public Session() {
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

    public Packet getResult() {
        return result;
    }

    public void setResult(Packet result) {
        this.result = result;
    }

    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(ParseStatus parseStatus) {
        this.parseStatus = parseStatus;
    }



}
