package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路由的结果(包含路由目标)
 * User: zhangyong
 * Date: 2015/3/14
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class RouteResultSet {

    private final static Logger logger = LoggerFactory.getLogger(RouteResultSet.class);

    private final String statement;

    public RouteResultSetNode[] nodes;

    public RouteResultSet(String stmt) {
        this.statement = stmt;
    }

    public RouteResultSetNode[] getNodes() {
        return nodes;
    }

    public void setNodes(RouteResultSetNode[] nodes) {
        this.nodes = nodes;
    }

}
