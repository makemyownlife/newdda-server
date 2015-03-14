package com.elong.pb.newdda.net;

import com.elong.pb.newdda.handler.FrontAuthHandler;
import com.elong.pb.newdda.handler.FrontQueryHandler;
import com.elong.pb.newdda.packet.BinaryPacket;
import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhangyong on 15/2/13.
 */
public class FrontDdaChannel implements DdaChannel {

    private static AcceptIdGenerator acceptIdGenerator = new AcceptIdGenerator();

    private AtomicReference<FrontBackendSession> FRONT_BACKEND_SESSION_REF = new AtomicReference<FrontBackendSession>();

    private Channel channel;

    private Long id;

    private byte[] seed;

    private FrontAuthHandler frontAuthHandler;

    private FrontQueryHandler frontQueryHandler;

    //是否自动提交
    private volatile boolean autocommit;

    //是否验证过了
    private volatile boolean isAuthenticated;

    public FrontDdaChannel(Channel channel) {
        this.channel = channel;
        this.id = acceptIdGenerator.getId();
        this.frontAuthHandler = new FrontAuthHandler(this);
        this.frontQueryHandler = new FrontQueryHandler(this);
        this.isAuthenticated = false;
        this.autocommit = true;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Long getId() {
        return id;
    }

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public FrontQueryHandler getFrontQueryHandler() {
        return frontQueryHandler;
    }

    public FrontBackendSession getCurrentFrontBackendSession() {
        return FRONT_BACKEND_SESSION_REF.get();
    }

    public void setCurrentFrontBackendSession(FrontBackendSession session){
        FRONT_BACKEND_SESSION_REF.set(session);
    }

    /**
     * 前端连接ID生成器
     *
     * @author xianmao.hexm
     */
    private static class AcceptIdGenerator {

        private static final long MAX_VALUE = 0xffffffffL;

        private long acceptId = 0L;
        private final Object lock = new Object();

        private long getId() {
            synchronized (lock) {
                if (acceptId >= MAX_VALUE) {
                    acceptId = 0L;
                }
                return ++acceptId;
            }
        }

    }

    public BinaryPacket handle(ByteBuffer byteBuffer) throws Exception {
        //首先验证链接
        if (!isAuthenticated()) {
            this.frontAuthHandler.handle(byteBuffer);
            return null;
        }
        //验证通过 则通过handler来处理
        BinaryPacket binaryPacket = new BinaryPacket(byteBuffer);
        return binaryPacket;
    }

    public void write(Object msg) {
        this.channel.writeAndFlush(msg);
    }


}
