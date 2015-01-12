package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.mysql.CommandPacket;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.net.mysql.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhangyong on 15/1/8.
 * 选择后端数据链接执行器
 */
public class NettySessionExecutor {

    private final static Logger logger = LoggerFactory.getLogger(NettySessionExecutor.class);

    private final AtomicReference<Session> currentSession = new AtomicReference<Session>();

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
        currentBackendChannel.getChannel().writeAndFlush(packet);

        
        return null;
    }

}
