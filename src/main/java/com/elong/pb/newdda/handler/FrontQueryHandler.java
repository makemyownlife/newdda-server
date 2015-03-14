package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.FrontBackendSession;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.parser.ServerParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/2/13.
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
        }
        if (sql == null || sql.length() == 0) {
            logger.error("前端链接:{} 传递sql为空，请注意!", frontDdaChannel);
        }
        // /* mysql-connector-java-5.1.28 ( Revision: alexander.soklakov@oracle.com-20131125092425-yvejy3xvci77ru3k ) */SHOW VARIABLES WHERE Variable_name ='language' OR Variable_name = 'net_write_timeout' OR Variable_name = 'interactive_timeout' OR Variable_name = 'wait_timeout' OR Variable_name = 'character_set_client' OR Variable_name = 'character_set_connection' OR Variable_name = 'character_set' OR Variable_name = 'character_set_server' OR Variable_name = 'tx_isolation' OR Variable_name = 'transaction_isolation' OR Variable_name = 'character_set_results' OR Variable_name = 'timezone' OR Variable_name = 'time_zone' OR Variable_name = 'system_time_zone' OR Variable_name = 'lower_case_table_names' OR Variable_name = 'max_allowed_packet' OR Variable_name = 'net_buffer_length' OR Variable_name = 'sql_mode' OR Variable_name = 'query_cache_type' OR Variable_name = 'query_cache_size' OR Variable_name = 'init_connect'
        int rs = ServerParse.parse(sql);
        if (logger.isInfoEnabled()) {
            logger.info("sql==" + sql + " rs==" + (rs & 0xff));
        }
        //创建一次 前后端的一次会话
        FrontBackendSession session = frontDdaChannel.getCurrentFrontBackendSession();
        if (session == null) {
            session = new FrontBackendSession(frontDdaChannel);
            frontDdaChannel.setCurrentFrontBackendSession(session);
        }
        session.execute(sql);
    }

}
