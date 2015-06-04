package com.elong.pb.newdda.route;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.recognizer.SQLParserDelegate;
import com.alibaba.cobar.parser.recognizer.mysql.syntax.MySQLParser;
import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.net.BackendChannelPool;
import com.elong.pb.newdda.route.util.PartitionUtil;
import com.elong.pb.newdda.server.BackendServer;
import com.elong.pb.newdda.server.DdaConfig;
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
        try {
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

            SQLStatement ast = SQLParserDelegate.parse(sql, MySQLParser.DEFAULT_CHARSET);
            PartitionKeyVisitor visitor = new PartitionKeyVisitor(dataSourceConfig);
            ast.accept(visitor);

            //判断列的值
            Map<String, Map<String, List<Object>>> astExt = visitor.getColumnValue();
            Map<String, List<Object>> currentCol2Val = null;
            for (Map.Entry<String, Map<String, List<Object>>> e : astExt.entrySet()) {
                Map<String, List<Object>> col2Val = e.getValue();
                if (col2Val == null || col2Val.isEmpty()) {
                    continue;
                }
                currentCol2Val = col2Val;
            }

            RouteResultSetNode[] nodes = null;
            //匹配规则
            boolean matchRule = false;
            if (currentCol2Val != null) {
                List<Object> objects = currentCol2Val.get(dataSourceConfig.getKeyname().toUpperCase());
                int size = objects.size();
                //仅匹配一个时候 进入同一个库
                if (size == 1) {
                    int[] count = new int[]{1, 1};
                    int[] length = new int[]{512, 512};
                    PartitionUtil pu = new PartitionUtil(count, length);
                    int index = pu.partition(objects.get(0).toString(), 0, 20);
                    logger.info("sql:{} index:{} hashcode:{}", sql, index ,objects.get(0).hashCode());
                    nodes = new RouteResultSetNode[1];
                    RouteResultSetNode node = new RouteResultSetNode(sql, LOCATION_POOL_MAPPING.get(locationList.get(index)));
                    nodes[0] = node;
                    matchRule = true;
                }
            }

            if (!matchRule) {
                nodes = new RouteResultSetNode[locationList.size()];
                int cursor = 0;
                for (DataSourceLocation location : locationList) {
                    RouteResultSetNode node = new RouteResultSetNode(sql, LOCATION_POOL_MAPPING.get(location));
                    nodes[cursor] = node;
                    cursor++;
                }
            }
            routeResultSet.setNodes(nodes);
            return routeResultSet;
        } catch (Exception e) {
            logger.error("error route sql:{} dataSourceId:{}", sql, dataSourceId);
            return null;
        }
    }

}
