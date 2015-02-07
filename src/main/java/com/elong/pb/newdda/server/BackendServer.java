package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    private BackendClient backendClient;

    private DdaConfig ddaConfig;

    public DdaConfig getDdaConfig() {
        return ddaConfig;
    }

    public BackendServer() {
        this.ddaConfig = new DdaConfig();
        this.backendClient = BackendClient.getInstance();
    }

    public void start() {
        Map<String, DataSourceConfig> dataSources = this.ddaConfig.getDataSources();
    }

    public void stop() {
    }

}
