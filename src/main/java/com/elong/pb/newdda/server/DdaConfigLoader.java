package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.loader.DataSourceConfigLoader;
import com.elong.pb.newdda.config.loader.RuleConfigLoader;
import com.elong.pb.newdda.config.loader.SchemaConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 加载中间件的数据库配置
 * Created by zhangyong on 15/8/3.
 */
public class DdaConfigLoader {

    public static final Logger logger = LoggerFactory.getLogger(DdaConfigLoader.class);

    private final AtomicBoolean inited = new AtomicBoolean(false);

    private static DdaConfigLoader INSTANCE = new DdaConfigLoader();

    //数据源配置加载器
    private static DataSourceConfigLoader dataSourceConfigLoader = new DataSourceConfigLoader();

    //规则加载器
    private static RuleConfigLoader ruleConfigLoader = new RuleConfigLoader();

    //SHEMA加载器
    private static SchemaConfigLoader schemaConfigLoader = new SchemaConfigLoader();

    public static DdaConfigLoader getInstance() {
        return INSTANCE;
    }

    public void init() {
        if (this.inited.compareAndSet(false, true)) {
            //加载数据源配置
            dataSourceConfigLoader.loadDataSourceConfig();
            //加载分库规则配置
            ruleConfigLoader.loadRuleConfig();
            //加载模块配置
            schemaConfigLoader.loadSchemaConfig();
        }
    }

}
