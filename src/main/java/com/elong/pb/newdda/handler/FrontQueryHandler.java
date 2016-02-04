package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.FrontBackendSession;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.packet.ErrorPacketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 处理query Command
 * Created by zhangyong on 15/8/11.
 */
public class FrontQueryHandler implements Handler {

    private final static Logger logger = LoggerFactory.getLogger(FrontQueryHandler.class);

    private FrontDdaChannel frontDdaChannel;

    public FrontQueryHandler(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
    }

    @Override
    public void handle(ByteBuffer byteBuffer) throws Exception {
        BufferUtil.stepBuffer(byteBuffer, 5);
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);
        String sql = null;
        try {
            sql = new String(data, SystemConfig.DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("前端链接:{}无法转成默认字符集{}", frontDdaChannel, SystemConfig.DEFAULT_CHARSET);
            frontDdaChannel.write(
                    ErrorPacketFactory.errorMessage(
                            ErrorCode.ER_UNKNOWN_CHARACTER_SET ,
                            "Unknown charset '" + SystemConfig.DEFAULT_CHARSET + "'")
            );
            return;
        }
        if (sql == null || sql.length() == 0) {
            logger.error("前端链接:{} 传递sql为空，请注意!", frontDdaChannel);
            frontDdaChannel.write(
                    ErrorPacketFactory.errorMessage(
                            ErrorCode.ER_NOT_ALLOWED_COMMAND,
                            "Empty SQL")
            );
            return;
        }
        if(logger.isInfoEnabled()) {
            logger.info("front transfer sql :{}", sql);
        }
        FrontBackendSession frontBackendSession = frontDdaChannel.getFrontBackendSession();
        frontBackendSession.execute(sql);
    }

}
