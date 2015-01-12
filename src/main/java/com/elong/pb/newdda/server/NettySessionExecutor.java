package com.elong.pb.newdda.server;

import com.elong.pb.newdda.net.mysql.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangyong on 15/1/8.
 * 选择后端数据链接执行器
 */
public class NettySessionExecutor {

    private final static Logger logger = LoggerFactory.getLogger(NettySessionExecutor.class);

    public static Packet execute(String sql, int type, final long timeoutMillis) throws IOException {
        ConcurrentHashMap<String, NettyBackendChannel>
                connections =
                NettyBackendConnectManageHandler.getBackendConnections();
        NettyBackendChannel currentBackendChannel = null;
        for (Map.Entry<String, NettyBackendChannel> e : connections.entrySet()) {
            currentBackendChannel = e.getValue();
            break;
        }
        return null;
    }

}
