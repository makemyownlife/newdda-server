package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 整体的dda配置 包含数据源 规则配置
 * Created by zhangyong on 15/8/8.
 */
public class DdaConfigSingleton {

    private final static Logger logger = LoggerFactory.getLogger(DdaConfigSingleton.class);

    private static Object mutex = new Object();

    private static DdaConfigSingleton INSTANCE = null;

    public static DdaConfigSingleton getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (mutex) {
            INSTANCE = new DdaConfigSingleton();
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
