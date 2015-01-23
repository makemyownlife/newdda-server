package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.mysql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhangyong on 15/1/8.
 * 选择后端数据链接执行器
 */
public class NettySessionExecutor {

    private final static Logger logger = LoggerFactory.getLogger(NettySessionExecutor.class);

    public static final AtomicReference<Session> SESSION_REFERENCE = new AtomicReference<Session>();

    public static Packet execute(String sql, int type, final long timeoutMillis) throws IOException {
        ConcurrentHashMap<String, NettyBackendChannel>
                connections =
                NettyBackendConnectManageHandler.getBackendConnections();
        NettyBackendChannel currentBackendChannel = null;
        for (Map.Entry<String, NettyBackendChannel> e : connections.entrySet()) {
            currentBackendChannel = e.getValue();
            break;
        }
        CommandPacket packet = new CommandPacket();
        packet.packetId = 0;
        packet.command = MysqlPacket.COM_QUERY;
        packet.arg = sql.getBytes(SystemConfig.DEFAULT_CHARSET);
        //发送命令，然后处理
        Session session = SESSION_REFERENCE.get();
        CountDownLatch latch = new CountDownLatch(1);
        if (session == null) {
            if(type == 7 || type == 9) {
                session = new SelectSession();
            }
            if(type == 8) {
                session = new SetSession();
            }
            SESSION_REFERENCE.set(session);
        }
        session.setLatch(latch);
        currentBackendChannel.getChannel().writeAndFlush(packet);
        try {
            session.getLatch().await(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("session返回的包是:{}", session.getResult());
        return session.getResult();
    }

}
