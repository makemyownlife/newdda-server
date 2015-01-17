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
public class Session {

    private final static Logger logger = LoggerFactory.getLogger(Session.class);

    public static enum ParseStatus {
        NULL, HEADER, FIELD, FIELD_EOF, ROWDATA, LAST_EOF
    }

    private CountDownLatch latch;

    private String uuid;

    private volatile ParseStatus parseStatus = ParseStatus.NULL;

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

    //收到
    public boolean decode(MysqlPacket mysqlPacket) {
        BinaryPacket temp = (BinaryPacket) mysqlPacket;
        temp.setReturn(true);
        switch (this.parseStatus) {
            //第一步是null
            case NULL:
                logger.info("解析resultset的头部");
                this.parseStatus = ParseStatus.HEADER;
                this.result = temp;
                temp.setReturn(true);
                countDownLatch();
                return true;
            case HEADER:
                logger.info("解析field 第四个字节：" + temp.getByteBuffer().get(4));
                if(temp.getByteBuffer().get(4) == EOFPacket.FIELD_COUNT) {
                    this.parseStatus = ParseStatus.FIELD_EOF;
                }
                temp.setReturn(true);
                this.result = null;
                NettyFrontConnetManageHandler.getNettyFrontChannel().getChannel()
                        .writeAndFlush(temp);
                return true;
            case FIELD_EOF:
                logger.info("从FIELD_EOF解析ROWDATA");
                this.parseStatus = ParseStatus.ROWDATA;
                temp.setReturn(true);
                this.result = null;
                NettyFrontConnetManageHandler.getNettyFrontChannel().getChannel()
                        .writeAndFlush(temp);
                return true;
            case ROWDATA:
                logger.info("从ROWDATA解析到LAST_EOF");
                if(temp.getByteBuffer().get(4) == EOFPacket.FIELD_COUNT) {
                    this.parseStatus = ParseStatus.LAST_EOF;
                }
                temp.setReturn(true);
                this.result = null;
                NettyFrontConnetManageHandler.getNettyFrontChannel().getChannel()
                        .writeAndFlush(temp);
                return true;
            case LAST_EOF:
                logger.info("从解析LAST_EOF");
                temp.setReturn(true);
                this.result = null;
                NettyFrontConnetManageHandler.getNettyFrontChannel().getChannel()
                        .writeAndFlush(temp);
                return true;
        }
        return false;
    }

}
