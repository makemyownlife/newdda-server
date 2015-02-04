package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/2/2.
 * 后端服务 启动mysql服务
 */
public class BackendServer {

    private final static Logger logger = LoggerFactory.getLogger(BackendServer.class);

    public static BackendServer INSTANCE = new BackendServer();

    public static BackendServer getInstance() {
        return INSTANCE;
    }

    private DdaConfig ddaConfig;

    public BackendServer() {
        this.ddaConfig = new DdaConfig();
    }

    public void start() {

    }

}
