package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.net.BackendChannelPool;
import com.elong.pb.newdda.server.BackendServer;
import com.elong.pb.newdda.server.DdaConfig;
import com.elong.pb.newdda.server.DdaServer;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyong on 15/2/10.
 * 前端连线相关 寻找相关的
 */
public class DdaRoute {

    private static final Logger logger = LoggerFactory.getLogger(DdaRoute.class);

    private static ConcurrentHashMap<DataSourceLocation, BackendChannelPool> LOCATION_POOL_MAPPING = new ConcurrentHashMap<DataSourceLocation, BackendChannelPool>();

    public static void addLocationPoolMapping(DataSourceLocation location, BackendChannelPool pool) {
        LOCATION_POOL_MAPPING.put(location, pool);
    }

    public static BackendChannelPool getPoolByLocation(DataSourceLocation location) {
        return LOCATION_POOL_MAPPING.get(location);
    }

    public static RouteResultSet route(String sql, String dataSourceId) {
        DdaConfig ddaConfig = BackendServer.getInstance().getDdaConfig();
        Map<String, DataSourceConfig> dataSources = ddaConfig.getDataSources();
        //是否包含数据源编号
        if (!dataSources.containsKey(dataSourceId)) {
            logger.error("cant find dataSourceId:{}", dataSourceId);
            return null;
        }
        //判断sql 应该定位于 哪个dataSourceLocation上
        DataSourceConfig dataSourceConfig = dataSources.get(dataSourceId);
        List<DataSourceLocation> locationList = dataSourceConfig.getDataSourceLocationList();
        RouteResultSet routeResultSet = new RouteResultSet(sql);
        RouteResultSetNode[] nodes = new RouteResultSetNode[locationList.size()];
        int cursor = 0;
        for (DataSourceLocation location : locationList) {
            RouteResultSetNode node = new RouteResultSetNode(sql, LOCATION_POOL_MAPPING.get(location));
            nodes[cursor] = node;
            cursor++;
        }
        routeResultSet.setNodes(nodes);
        return routeResultSet;
    }

}
