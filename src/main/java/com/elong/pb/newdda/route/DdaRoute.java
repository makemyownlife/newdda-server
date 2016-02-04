package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.config.SchemaConfig;
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

    public static RouteResultSet route(String sql, String schemaId) {
        DdaConfigSingleton ddaConfig = DdaConfigSingleton.getInstance();
        Map<String,SchemaConfig> schemas = ddaConfig.getSchemas();
        //是否包含(数据源编号)
        if (!schemas.containsKey(schemaId)) {
            logger.error("cant find schemaId:{} from schema.xml", schemaId);
            return null;
        }
        //计算结果集
        RouteResultSet routeResultSet = new RouteResultSet(sql);
        return null;
    }

}
