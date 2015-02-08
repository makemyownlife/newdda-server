package com.elong.pb.newdda.net;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 15/2/8.
 * dda后端连接 （也就是mysql链接）
 */
public class BackendDdaChannel implements DdaChannel {

    private static ConnectIdGenerator CONNECT_ID_GENERATOR = new ConnectIdGenerator();

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

    public BackendDdaChannel(ChannelWrapper channelWrapper) {
        this.id = CONNECT_ID_GENERATOR.getId();
        this.autocommit = true;
        this.isAuthenticated = false;
        this.channelWrapper = channelWrapper;
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

}
