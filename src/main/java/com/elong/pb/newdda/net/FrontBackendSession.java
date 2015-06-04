package com.elong.pb.newdda.net;

import com.elong.pb.newdda.packet.BinaryPacket;
import com.elong.pb.newdda.route.DdaRoute;
import com.elong.pb.newdda.route.RouteResultSet;
import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
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

    //单节点执行器
    private final SingleNodeExecutor singleNodeExecutor;

    //多节点执行器
    private final MultiNodeExecutor multiNodeExecutor;

    //是否多节点
    private volatile boolean isMultiNode = false;

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

    public FrontDdaChannel getFrontDdaChannel() {
        return frontDdaChannel;
    }

    public void execute(String sql) {
        RouteResultSet routeResultSet = DdaRoute.route(sql, frontDdaChannel.getDataSource());
        if (routeResultSet == null) {
            logger.error("sql:{}路由异常");
            return;
        }
        RouteResultSetNode[] nodes = routeResultSet.getNodes();
        if (nodes == null) {
            logger.error("sql:{}无法找到后端连接");
            return;
        }

        this.isMultiNode = false;
        //组装后端的链接
        for (int i = 0; i < nodes.length; i++) {
            RouteResultSetNode node = nodes[i];
            BackendDdaChannel backendDdaChannel = node.getBackendChannelPool().getBackendDdaChannelFromPool();
            if (backendDdaChannel == null) {
                logger.error("找不到node:{} 对应的后端mysql链接", node);
                return;
            }
            target.put(node, backendDdaChannel);
        }

        multiNodeExecutor.execute(nodes, this, sql);

    }

    public void dispatch(BackendDdaChannel backendDdaChannel, ByteBuffer byteBuffer) {
        BinaryPacket binaryPacket = new BinaryPacket(byteBuffer);
        multiNodeExecutor.asyncMysqlPacket(backendDdaChannel, binaryPacket);
    }

    public void release() {
        //释放链接,并且设置相关的参数
        Iterator iterator = target.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RouteResultSetNode, BackendDdaChannel> entry = (Map.Entry) iterator.next();
            BackendDdaChannel backendDdaChannel = entry.getValue();
            backendDdaChannel.getBackendChannelPool().releaseBackendChannelIntoPool(backendDdaChannel);
        }
    }

}
