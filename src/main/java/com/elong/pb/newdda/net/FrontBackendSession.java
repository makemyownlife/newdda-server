package com.elong.pb.newdda.net;

import com.elong.pb.newdda.route.DdaRoute;
import com.elong.pb.newdda.route.RouteResultSet;
import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyong on 15/2/11.
 * 前后端一次会话
 * 其实是在翻译cobar 按照我的方式来做，希望做一个尝试。
 */
public class FrontBackendSession {

    private static Logger logger = LoggerFactory.getLogger(FrontBackendSession.class);

    //前端链接
    private FrontDdaChannel frontDdaChannel;

    private final SingleNodeExecutor singleNodeExecutor;

    private final MultiNodeExecutor multiNodeExecutor;

    private ConcurrentHashMap<RouteResultSetNode, BackendDdaChannel> target;

    public FrontBackendSession(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
        this.singleNodeExecutor = new SingleNodeExecutor();
        this.multiNodeExecutor = new MultiNodeExecutor();
        this.target = new ConcurrentHashMap<RouteResultSetNode, BackendDdaChannel>();
    }

    //当前需要执行sql对应的后端链接
    public ConcurrentHashMap<RouteResultSetNode, BackendDdaChannel> getTarget() {
        return target;
    }

    public void execute(String sql) {
        RouteResultSet routeResultSet = DdaRoute.route(sql, frontDdaChannel.getDataSource());
        RouteResultSetNode[] nodes = routeResultSet.getNodes();
        if (nodes == null) {
            logger.error("sql:{}无法找到后端连接");
            return;
        }
        if (nodes.length == 1) {
            singleNodeExecutor.execute(nodes[0], this, sql);
        }
        //多节点执行命令
        else {
            multiNodeExecutor.execute(nodes, this, sql);
        }
    }

}
