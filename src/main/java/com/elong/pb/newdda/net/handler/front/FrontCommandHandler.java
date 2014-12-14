package com.elong.pb.newdda.net.handler.front;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.config.ErrorCode;
import com.elong.pb.newdda.config.SystemConfig;
import com.elong.pb.newdda.net.handler.NettyHandler;
import com.elong.pb.newdda.net.mysql.BinaryPacket;
import com.elong.pb.newdda.net.mysql.ErrorPacketFactory;
import com.elong.pb.newdda.net.mysql.MysqlPacket;
import com.elong.pb.newdda.net.mysql.Packet;
import com.elong.pb.newdda.server.NettyFrontChannel;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 14/12/6.
 * 命令相关
 */
public class FrontCommandHandler implements NettyHandler {

    private static Logger logger = LoggerFactory.getLogger(FrontCommandHandler.class);

    private NettyFrontChannel nettyFrontChannel;

    public FrontCommandHandler(NettyFrontChannel nettyFrontChannel) {
        this.nettyFrontChannel = nettyFrontChannel;
    }

    @Override
    public MysqlPacket handle(ByteBuf byteBuf) {
        if (byteBuf != null && byteBuf.readableBytes() == 0) {
            return null;
        }
        int readableLength = byteBuf.readableBytes();
        //前3个字节表示
        if (readableLength < 3) {
            return null;
        }
        byteBuf.markReaderIndex();
        int readerIndex = byteBuf.readerIndex();

        //读取packet的长度 前3个字节
        int length = byteBuf.getByte(readerIndex) & 0xff;
        length |= (byteBuf.getByte(++readerIndex) & 0xff) << 8;
        length |= (byteBuf.getByte(++readerIndex) & 0xff) << 16;

        //数据包不够解析直接返回
        if (readableLength < length + 3 + 1) {
            byteBuf.resetReaderIndex();
            return null;
        }
        byteBuf.resetReaderIndex();

        //完整的数据包
        ByteBuf data = byteBuf.slice(byteBuf.readerIndex(), length + 1 + 3);
        ByteBuffer byteBuffer = data.nioBuffer();

        //直接发送二进制数据
        BinaryPacket binaryPacket = new BinaryPacket(byteBuffer);
        if (logger.isDebugEnabled()) {
            logger.debug("binaryPacket==" + binaryPacket);
        }
        return binaryPacket;
    }

    @Override
    //handler处理
    public Packet handle(MysqlPacket mysqlPacket) {
        BinaryPacket binaryPacket = (BinaryPacket) mysqlPacket;
        ByteBuffer byteBuffer = binaryPacket.getByteBuffer();

        Packet packet = null;
        switch (byteBuffer.get(4)) {
            case MysqlPacket.COM_INIT_DB:
                logger.info("处理INIT_DB命令");
                break;
            case MysqlPacket.COM_QUERY:
                logger.info("处理COM_QUERY命令");
                packet =  handleQuery(byteBuffer);
                break;
            default:
                //unknown command
        }
        return packet;
    }

    //处理查询相关
    private Packet handleQuery(ByteBuffer byteBuffer) {
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
            return ErrorPacketFactory.createErrorPacket((byte) 1,ErrorCode.ER_NOT_ALLOWED_COMMAND, "Empty SQL");
        }
        //    /* mysql-connector-java-5.1.28 ( Revision: alexander.soklakov@oracle.com-20131125092425-yvejy3xvci77ru3k ) */SHOW VARIABLES WHERE Variable_name ='language' OR Variable_name = 'net_write_timeout' OR Variable_name = 'interactive_timeout' OR Variable_name = 'wait_timeout' OR Variable_name = 'character_set_client' OR Variable_name = 'character_set_connection' OR Variable_name = 'character_set' OR Variable_name = 'character_set_server' OR Variable_name = 'tx_isolation' OR Variable_name = 'transaction_isolation' OR Variable_name = 'character_set_results' OR Variable_name = 'timezone' OR Variable_name = 'time_zone' OR Variable_name = 'system_time_zone' OR Variable_name = 'lower_case_table_names' OR Variable_name = 'max_allowed_packet' OR Variable_name = 'net_buffer_length' OR Variable_name = 'sql_mode' OR Variable_name = 'query_cache_type' OR Variable_name = 'query_cache_size' OR Variable_name = 'init_connect'
        logger.info("sql==" + sql);
        return null;
    }

}
