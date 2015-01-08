package com.elong.pb.newdda.server;

import com.elong.pb.newdda.net.mysql.Packet;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by zhangyong on 15/1/8.
 * 选择后端数据链接执行器
 */
public class NettySessionExecutor {

    private final static Logger logger = LoggerFactory.getLogger(NettySessionExecutor.class);

    public static Packet execute(NettyBackendChannel nettyBackendChannel,  final long timeoutMillis) throws IOException {
        if (nettyBackendChannel == null) {
            logger.info("错误的链接");
        }
        Channel channel = nettyBackendChannel.getChannel();
        return null;
    }

}
