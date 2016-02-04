package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.common.RemotingHelper;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SchemaConfig;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.packet.AuthPacket;
import com.elong.pb.newdda.net.packet.BinaryMySqlPacket;
import com.elong.pb.newdda.net.packet.ErrorPacket;
import com.elong.pb.newdda.server.DdaConfigSingleton;
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
        AuthPacket authPacket = new AuthPacket();
        authPacket.decode(byteBuffer);

        boolean databaseFlag = true;
        //开始验证用户名 密码是否正确
        String database = authPacket.getDatabase();
        if (database == null || "".equals(database)) {
            databaseFlag = false;
        } else {
            Map<String, SchemaConfig> schemas = DdaConfigSingleton.getInstance().getSchemas();
            if (!schemas.containsKey(database)) {
                databaseFlag = false;
            }
        }
        if (!databaseFlag) {
            logger.error("cant find front dateSource: {} ,please check schema config ", database);
            ErrorPacket errorPacket = new ErrorPacket();
            errorPacket.packetId = (byte) 2;
            errorPacket.setErrno(ErrorCode.ER_ACCESS_DENIED_ERROR);
            errorPacket.setMsg("Access denied for database '" + authPacket.getDatabase() + "'");
            this.frontDdaChannel.write(errorPacket);
            return;
        }

        StringBuilder s = new StringBuilder();
        s.append(RemotingHelper.parseChannelRemoteAddr(frontDdaChannel.getChannel())).append('\'').append(authPacket.getUser()).append("' login success");
        byte[] extra = authPacket.getExtra();
        if (extra != null && extra.length > 0) {
            s.append(",extra:").append(new String(extra));
        }
        logger.info(s.toString());
        //设置标志位
        this.frontDdaChannel.setAuthenticated(true);
        //设置当前的虚拟数据库(虚拟的节点)
        this.frontDdaChannel.setSchemaId(database);
        ByteBuffer authOkByteBuffer = ByteBuffer.allocate(AUTH_OK.length);
        authOkByteBuffer.put(AUTH_OK);
        authOkByteBuffer.flip();
        BinaryMySqlPacket authOkPacket = new BinaryMySqlPacket(authOkByteBuffer, false);
        this.frontDdaChannel.write(authOkPacket);
    }

}
