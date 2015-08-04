package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后端链接服务的启动 管理mysql相关
 * Created by zhangyong on 15/7/9.
 */
public class BackendServer implements BaseServer {

    private final Logger logger = LoggerFactory.getLogger(BackendServer.class);

    private static Object mutex = new Object();

    private static BackendServer INSTANCE = null;

    public static BackendServer getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (mutex) {
            INSTANCE = new BackendServer();
            return INSTANCE;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
