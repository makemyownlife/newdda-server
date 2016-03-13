package com.elong.pb.newdda.net;

import com.elong.pb.newdda.handler.BackendAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 后端链接mysql的链接
 * Created by zhangyong on 15/7/23.
 */
public class BackendDdaChannel extends DdaChannel {

    private final static Logger logger = LoggerFactory.getLogger(BackendDdaChannel.class);

    private static ConnectIdGenerator backendIdGenerator = new ConnectIdGenerator();

    private BackendAuthHandler backendAuthHandler;

    private Long id;

    private volatile boolean authenticated;

    private BackendDdaChannelPool backendDdaChannelPool;

    //channel封装相关
    private ChannelWrapper channelWrapper;

    //是否正在执行sql语句 或者在等待sql返回
    private volatile boolean isRunning;

    private AtomicBoolean isClosed;

    public BackendDdaChannel(ChannelWrapper channelWrapper, BackendDdaChannelPool backendDdaChannelPool) {
        this.id = backendIdGenerator.getId();
        this.authenticated = false;
        this.channelWrapper = channelWrapper;
        this.backendDdaChannelPool = backendDdaChannelPool;
        this.isRunning = false;
        this.isClosed = new AtomicBoolean(false);
        //验证的处理器
        this.backendAuthHandler = new BackendAuthHandler(this);
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

    //==================================================================================get set method start =================================================================================================
    public Long getId() {
        return id;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public BackendDdaChannelPool getBackendDdaChannelPool() {
        return backendDdaChannelPool;
    }

    public ChannelWrapper getChannelWrapper() {
        return channelWrapper;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public AtomicBoolean getIsClosed() {
        return isClosed;
    }

    public BackendAuthHandler getBackendAuthHandler() {
        return backendAuthHandler;
    }

   //==================================================================================get set method end =================================================================================================
    public void write(Object message) {
        this.getChannelWrapper().getChannel().writeAndFlush(message);
    }

}
