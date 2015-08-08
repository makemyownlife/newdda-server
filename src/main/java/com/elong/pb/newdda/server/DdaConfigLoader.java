package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 加载中间件的数据库配置
 * Created by zhangyong on 15/8/3.
 */
public class DdaConfigLoader {

    public static final Logger logger = LoggerFactory.getLogger(DdaConfigLoader.class);

    private final AtomicBoolean inited = new AtomicBoolean(false);

    private static DdaConfigLoader INSTANCE = new DdaConfigLoader();

    public static DdaConfigLoader getInstance(){
        return INSTANCE;
    }

    public void init(){
        if (this.inited.compareAndSet(false, true)) {
            //加载数据源配置
            loadDataSourceConfig();
            //加载模块配置
            loadSchemaConfig();
            //加载分库规则配置
            loadRuleConfig();
        }
    }

    private void loadDataSourceConfig(){

    }

    private void loadSchemaConfig(){

    }

    private void loadRuleConfig(){

    }


}
