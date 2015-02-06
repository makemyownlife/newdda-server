package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.loader.XmlConfigLoader;

import java.util.Map;

/**
 * Created by zhangyong on 15/2/2.
 * 总的dda的配置文件
 */
public class DdaConfig {

    private volatile Map<String, DataSourceConfig> dataSources;

    public DdaConfig() {
        XmlConfigLoader loader = new XmlConfigLoader();
        this.dataSources = loader.getDataSources();
    }

    public Map<String, DataSourceConfig> getDataSources() {
        return this.dataSources;
    }

}
