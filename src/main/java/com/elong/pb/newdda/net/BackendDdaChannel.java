package com.elong.pb.newdda.net;

import com.elong.pb.newdda.handler.BackendAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhangyong on 15/2/8.
 * dda后端连接 （也就是mysql链接）
 */
public class BackendDdaChannel implements DdaChannel {

    private final static Logger logger = LoggerFactory.getLogger(BackendDdaChannel.class);

    private static ConnectIdGenerator CONNECT_ID_GENERATOR = new ConnectIdGenerator();

    private AtomicReference<FrontBackendSession> CURRENT_SESSION = new AtomicReference<FrontBackendSession>();

    private BackendAuthHandler backendAuthHandler;

    private BackendChannelPool backendChannelPool;

    //channel封装相关
    private ChannelWrapper channelWrapper;

    //是否自动提交
    private volatile boolean autocommit;

    //是否验证过了
    private volatile boolean isAuthenticated;

    //是否正在执行sql语句 或者在等待sql返回
    private volatile boolean isRunning;

    private AtomicBoolean isClosed;

    private Long id;

    private String charset;

    public BackendDdaChannel(ChannelWrapper channelWrapper, BackendChannelPool backendChannelPool) {
        this.id = CONNECT_ID_GENERATOR.getId();
        this.autocommit = true;
        this.isAuthenticated = false;
        this.channelWrapper = channelWrapper;
        this.backendChannelPool = backendChannelPool;
        this.backendAuthHandler = new BackendAuthHandler(this);
        this.isRunning = false;
        this.isClosed = new AtomicBoolean(false);
    }

    //后端ID生成器
    private static class ConnectIdGenerator {
        private static final long MAX_VALUE = Long.MAX_VALUE;
        private long connectId = 0L;
        private final Object lock = new Object();

        private long getId() {
            synchronized (lock) {
                if (connectId >= MAX_VALUE) {
                    connectId = 0L;
                }
                return ++connectId;
            }
        }
    }

    public boolean isClosedOrQuit() {
        return isClosed.get();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public AtomicBoolean getIsClosed() {
        return isClosed;
    }

    public ChannelWrapper getChannelWrapper() {
        return channelWrapper;
    }

    public BackendChannelPool getBackendChannelPool() {
        return backendChannelPool;
    }

    public void setBackendChannelPool(BackendChannelPool backendChannelPool) {
        this.backendChannelPool = backendChannelPool;
    }

    public BackendAuthHandler getBackendAuthHandler() {
        return backendAuthHandler;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("后端链接编号:").append(this.id);
        return sb.toString();
    }

    //==================================================== 处理消息相关 ====================================================
    //处理mysql的消息
    public void handle(ByteBuffer byteBuffer) throws Exception {
        //没有验证，则需要发送auth 相关的命令
        if (!isAuthenticated) {
            backendAuthHandler.handle(byteBuffer);
            return;
        }
        FrontBackendSession currentSession = CURRENT_SESSION.get();
        //是否存在前端session,若是前端
        if (currentSession != null) {

        }
    }

    //发送mysql包
    public void write(Object msg) {
        this.getChannelWrapper().getChannel().writeAndFlush(msg);
    }

}