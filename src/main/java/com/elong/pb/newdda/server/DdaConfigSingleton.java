package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.rule.RuleAlgorithm;
import com.elong.pb.newdda.config.rule.RuleConfig;
import com.elong.pb.newdda.config.rule.TableRuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    //规则相关的类
    private final Map<String, RuleAlgorithm> functions = new HashMap<String, RuleAlgorithm>();

    public Map<String, RuleAlgorithm> getFunctions() {
        return functions;
    }

    private final Map<String, TableRuleConfig> tableRules = new HashMap<String, TableRuleConfig>();

    public Map<String, TableRuleConfig> getTableRules() {
        return tableRules;
    }

    private Set<RuleConfig> rules = new HashSet<RuleConfig>();

    public Set<RuleConfig> getRules() {
        return rules;
    }

    //schema
    

}
