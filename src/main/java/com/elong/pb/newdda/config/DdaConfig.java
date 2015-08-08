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

    //数据源
    private volatile Map<String, DataSourceConfig> dataSources;


}
