package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.net.BackendDataSource;
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
        //初始化后端连接池
        Map<String, DataSourceConfig> dataSources = this.ddaConfig.getDataSources();
        for (String dataSourceId : dataSources.keySet()) {
            DataSourceConfig dataSourceConfig = dataSources.get(dataSourceId);
            BackendDataSource backendDataSource = new BackendDataSource(dataSourceConfig);
            backendDataSource.init();
        }
        //路由相关初始化
    }

    public void stop() {

    }

}
