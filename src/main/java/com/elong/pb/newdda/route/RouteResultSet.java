package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.DataSourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由结果集
 * Created by zhangyong on 15/8/25.
 */
public class RouteResultSet {

    private final static Logger logger = LoggerFactory.getLogger(RouteResultSet.class);

    private String statement;

    private List<DataSourceLocation> nodes;

    public RouteResultSet(String statement) {
        this.statement = statement;
        this.nodes = new ArrayList<DataSourceLocation>();
    }

    //=============================================set  get method ===================================================
    public String getStatement() {
        return statement;
    }

    public List<DataSourceLocation> getNodes() {
        return nodes;
    }

}
