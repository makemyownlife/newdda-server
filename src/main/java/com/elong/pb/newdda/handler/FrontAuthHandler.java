package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.packet.AuthPacket;
import com.elong.pb.newdda.server.DdaConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

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

        //开始验证用户名 密码是否正确
        String database = authPacket.getDatabase();
        if(database == null || "".equals(database)){
            
        }
    }

}
