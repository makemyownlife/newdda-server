package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.mysql.BinaryPacket;
import com.elong.pb.newdda.net.mysql.ErrorPacketFactory;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.parser.ServerParse;
import com.elong.pb.newdda.parser.ServerParseShow;
import com.elong.pb.newdda.server.NettySessionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/1/8.
 * 查询处理器
 */
public class QueryHandler {

    private final static Logger logger = LoggerFactory.getLogger(QueryHandler.class);

    public static Packet handle(MysqlPacket mysqlPacket) throws IOException {
        BinaryPacket binaryPacket = (BinaryPacket) mysqlPacket;

        ByteBuffer byteBuffer = binaryPacket.getByteBuffer();
        BufferUtil.stepBuffer(byteBuffer, 5);

        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);
        String sql = null;
        try {
            sql = new String(data, SystemConfig.DEFAULT_CHARSET);
        } catch (Exception e) {
            return ErrorPacketFactory.createErrorPacket((byte) 1, ErrorCode.ER_UNKNOWN_CHARACTER_SET, "Unknown charset '" + SystemConfig.DEFAULT_CHARSET + "'");
        }
        if (sql == null || sql.length() == 0) {
            return ErrorPacketFactory.createErrorPacket((byte) 1, ErrorCode.ER_NOT_ALLOWED_COMMAND, "Empty SQL");
        }
        //  /* mysql-connector-java-5.1.28 ( Revision: alexander.soklakov@oracle.com-20131125092425-yvejy3xvci77ru3k ) */SHOW VARIABLES WHERE Variable_name ='language' OR Variable_name = 'net_write_timeout' OR Variable_name = 'interactive_timeout' OR Variable_name = 'wait_timeout' OR Variable_name = 'character_set_client' OR Variable_name = 'character_set_connection' OR Variable_name = 'character_set' OR Variable_name = 'character_set_server' OR Variable_name = 'tx_isolation' OR Variable_name = 'transaction_isolation' OR Variable_name = 'character_set_results' OR Variable_name = 'timezone' OR Variable_name = 'time_zone' OR Variable_name = 'system_time_zone' OR Variable_name = 'lower_case_table_names' OR Variable_name = 'max_allowed_packet' OR Variable_name = 'net_buffer_length' OR Variable_name = 'sql_mode' OR Variable_name = 'query_cache_type' OR Variable_name = 'query_cache_size' OR Variable_name = 'init_connect'
        logger.info("sql==" + sql);
        int rs = ServerParse.parse(sql);

        Packet packet = null;
        switch (rs & 0xff) {
            case ServerParse.SHOW:
                int parseResult = ServerParseShow.parse(sql, rs >>> 8);
                logger.info("进行show相关的操作..操作类型:{}", parseResult);
                packet = NettySessionExecutor.execute(sql, ServerParse.SHOW, SystemConfig.DEFAULT_EXECUTE_TIME_OUT);
                break;
        }
        return packet;
    }

}
