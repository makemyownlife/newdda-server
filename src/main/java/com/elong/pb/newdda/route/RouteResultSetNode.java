package com.elong.pb.newdda.route;

import com.elong.pb.newdda.net.BackendChannelPool;

/**
 * 路由结果节点
 * User: zhangyong
 * Date: 2015/3/14
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class RouteResultSetNode {

    private final String statement; // 执行的语句

    private BackendChannelPool backendChannelPool;

    public RouteResultSetNode(String statement, BackendChannelPool backendChannelPool) {
        this.backendChannelPool = backendChannelPool;
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public BackendChannelPool getBackendChannelPool() {
        return backendChannelPool;
    }

}
