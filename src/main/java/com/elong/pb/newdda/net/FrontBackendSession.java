package com.elong.pb.newdda.net;

import com.elong.pb.newdda.route.DdaRoute;
import com.elong.pb.newdda.route.RouteResultSet;
import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/2/11.
 * 前后端一次会话
 * 其实是在翻译cobar 按照我的方式来做，希望做一个尝试。
 */
public class FrontBackendSession {

    private static Logger logger = LoggerFactory.getLogger(FrontBackendSession.class);

    private final static Long MAX_EXECUTE_TIME = 30 * 1000L;

    //前端链接
    private FrontDdaChannel frontDdaChannel;

    private final SingleNodeExecutor singleNodeExecutor;

    private final MultiNodeExecutor multiNodeExecutor;

    public FrontBackendSession(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
        this.singleNodeExecutor = new SingleNodeExecutor();
        this.multiNodeExecutor = new MultiNodeExecutor();
    }

    public void execute(String sql) {
        RouteResultSet routeResultSet = DdaRoute.route(sql, frontDdaChannel.getDataSource());
        RouteResultSetNode[] nodes = routeResultSet.getNodes();
        if (nodes == null) {
            logger.error("sql:{}无法找到后端连接");
            return;
        }
        if(nodes.length > 1) {
        }
    }

}
