package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
        //初始化后端连接池
        DdaConfigSingleton ddaConfigSingleton = DdaConfigSingleton.getInstance();
        Map<String, DataSourceConfig> dataSources = ddaConfigSingleton.getDataSources();
    }

    @Override
    public void stop() {

    }

}
