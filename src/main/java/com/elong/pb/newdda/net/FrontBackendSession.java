package com.elong.pb.newdda.net;

import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.net.packet.ErrorPacketFactory;
import com.elong.pb.newdda.route.DdaRoute;
import com.elong.pb.newdda.route.RouteResultSet;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前后端一次会话
 * Created by zhangyong on 15/8/19.
 */
public class FrontBackendSession {

    private final static Logger logger = LoggerFactory.getLogger(FrontBackendSession.class);

    private FrontDdaChannel frontDdaChannel;

    public FrontBackendSession(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
    }

    //执行sql
    public void execute(String sql) {
        RouteResultSet routeResultSet = null;
        try {
            routeResultSet = DdaRoute.route(sql, frontDdaChannel.getSchemaId());
        } catch (Exception e) {
            StringBuilder s = new StringBuilder();
            logger.warn(s.append(this).append(sql).toString(), e);
            String msg = e.getMessage();
            frontDdaChannel.write(
                    ErrorPacketFactory.errorMessage(
                            ErrorCode.ER_PARSE_ERROR,
                            msg == null ? e.getClass().getSimpleName() : msg));
            return;
        }
        if (routeResultSet == null || CollectionUtils.isEmpty(routeResultSet.getNodes())) {
            logger.error("sql:{}路由异常,没有办法找到相关的节点", sql);
            frontDdaChannel.write(
                    ErrorPacketFactory.errorMessage(
                            ErrorCode.ER_NO_DB_ERROR,
                            "No dataNode selected"));
            return;
        }
    }

    //释放相关的资源
    public void release() {

    }

}
