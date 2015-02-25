package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.packet.AuthPacket;
import com.elong.pb.newdda.packet.BinaryPacket;
import com.elong.pb.newdda.packet.ErrorPacket;
import com.elong.pb.newdda.server.BackendServer;
import com.elong.pb.newdda.server.DdaConfig;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by zhangyong on 15/2/13.
 * 后端链接验证 ,验证成功后才可以放到 可用连接池中
 */
public class FrontAuthHandler implements Handler {

    private final static Logger logger = LoggerFactory.getLogger(FrontAuthHandler.class);

    private static final byte[] AUTH_OK = new byte[]{7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0};

    private FrontDdaChannel frontDdaChannel;

    public FrontAuthHandler(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
    }

    @Override
    public void handle(ByteBuffer byteBuffer) throws Exception {
        //查看auth 验证端口
        AuthPacket authPacket = new AuthPacket();
        authPacket.decode(byteBuffer);
        String database = authPacket.getDatabase();
        //取消验证 用户名 ，密码 仅仅验证 虚拟数据库
        boolean databaseFlag = true;
        if (database == null || "".equals(database)) {
            databaseFlag = false;
        } else {
            DdaConfig ddaConfig = BackendServer.getInstance().getDdaConfig();
            Map<String, DataSourceConfig> dataSources = ddaConfig.getDataSources();
            if (!dataSources.containsKey(database)) {
                databaseFlag = false;
            }
        }
        if (!databaseFlag) {
            ErrorPacket errorPacket = new ErrorPacket();
            errorPacket.packetId = (byte) 2;
            errorPacket.errno = ErrorCode.ER_ACCESS_DENIED_ERROR;
            errorPacket.msg = "Access denied for database '" + authPacket.database + "'";
            this.frontDdaChannel.write(errorPacket);
        } else {
            Channel channel = frontDdaChannel.getChannel();
            StringBuilder s = new StringBuilder();
            s.append(RemotingHelper.parseChannelRemoteAddr(frontDdaChannel.getChannel())).append('\'').append(authPacket.user).append("' login success");
            byte[] extra = authPacket.extra;
            if (extra != null && extra.length > 0) {
                s.append(",extra:").append(new String(extra));
            }
            logger.info(s.toString());
            //设置标志位
            this.frontDdaChannel.setAuthenticated(true);
            ByteBuffer authOkByteBuffer = ByteBuffer.allocate(AUTH_OK.length);
            authOkByteBuffer.put(AUTH_OK);
            authOkByteBuffer.flip();
            BinaryPacket authOkPacket = new BinaryPacket(authOkByteBuffer);
            this.frontDdaChannel.write(authOkPacket);
        }
    }

}
