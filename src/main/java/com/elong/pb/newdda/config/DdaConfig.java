package com.elong.pb.newdda.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 整体的dda配置 包含数据源 规则配置
 * Created by zhangyong on 15/8/8.
 */
public class DdaConfig {

    private final static Logger logger = LoggerFactory.getLogger(DdaConfig.class);

    private static Object mutex = new Object();

    private static DdaConfig INSTANCE = null;

    public static DdaConfig getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (mutex) {
            INSTANCE = new DdaConfig();
            return INSTANCE;
        }
    }

    //数据源
    private Map<String, DataSourceConfig> dataSources = new HashMap<String, DataSourceConfig>();

    public Map<String, DataSourceConfig> getDataSources() {
        return this.dataSources;
    }

    //定义数据源以及schema

    //规则相关的类


}
