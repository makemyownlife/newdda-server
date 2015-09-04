package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.server.DdaConfigSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * dda路由工具类
 * Created by zhangyong on 15/8/25.
 */
public class DdaRoute {

    private static final Logger logger = LoggerFactory.getLogger(DdaRoute.class);

    public static RouteResultSet route(String sql, String dataSourceId) {
        DdaConfigSingleton ddaConfig = DdaConfigSingleton.getInstance();
        Map<String, DataSourceConfig> dataSources = ddaConfig.getDataSources();
        //是否包含(数据源编号)
        if (!dataSources.containsKey(dataSourceId)) {
            logger.error("cant find dataSourceId:{}", dataSourceId);
            return null;
        }

        //数据源配置
        DataSourceConfig dataSourceConfig = dataSources.get(dataSourceId);
        List<DataSourceLocation> locationList = dataSourceConfig.getDataSourceLocationList();

        //计算结果集
        RouteResultSet routeResultSet = new RouteResultSet(sql);

        return null;
    }

}
