package com.elong.pb.newdda.net.async;

import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.NodeExecutor;
import com.elong.pb.newdda.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyong on 15/4/4.
 */
public class QueryAsyncCommand implements AsyncCommand {

    private final static Logger logger = LoggerFactory.getLogger(QueryAsyncCommand.class);

    private final ReentrantLock lock = new ReentrantLock();

    //单个后端链接对应的解析状态
    private final ConcurrentHashMap<BackendDdaChannel, QueryParseStatus> queryParseStatusMapping;

    //HEADER|FIELDS|FIELD_EOF|ROWS|LAST_EOF
    public enum QueryParseStatus {
        HEADER, FIELD, ROWDATA, END
    }

    //头部
    private MysqlPacket headerPacket;

    // field packets
    private List<MysqlPacket> fieldPackets;

    private MysqlPacket filedEofPacket;

    private List<MysqlPacket> rowDataPackets;

    private MysqlPacket rowdataEndPacket;

    private volatile int filedCount;

    private volatile boolean fieldEOF;

    private byte packetId;

    private NodeExecutor nodeExecutor;

    //整个回话的解析状态 (需要和每个后端解析状态保持同步)
    private volatile QueryParseStatus overallQueryParseStatus;

    public QueryAsyncCommand(NodeExecutor executor) {
        this.fieldEOF = false;
        this.packetId = 0;
        this.filedCount = 0;
        this.nodeExecutor = executor;
        this.overallQueryParseStatus = QueryParseStatus.HEADER;
        this.queryParseStatusMapping = new ConcurrentHashMap<BackendDdaChannel, QueryParseStatus>();
        this.rowDataPackets = new ArrayList<MysqlPacket>();
    }

    @Override
    public void asyncMysqlPacket(BackendDdaChannel backendDdaChannel, MysqlPacket mysqlPacket) {
        BinaryPacket binaryPacket = (BinaryPacket) mysqlPacket;
        ByteBuffer byteBuffer = binaryPacket.getByteBuffer();
        byte b = byteBuffer.get(4);
        this.lock.lock();
        try {
            QueryParseStatus current = queryParseStatusMapping.get(backendDdaChannel);
            if (current == null) {
                current = QueryParseStatus.HEADER;
            }
            switch (current) {
                case HEADER:
                    if (b == ErrorPacket.FIELD_COUNT) {
                        logger.error("解析resulet set header 出错");
                        break;
                    }
                    if (overallQueryParseStatus == QueryParseStatus.FIELD) {
                        //已经解析了头部 直接将该状态设置已经解析了头部
                        queryParseStatusMapping.put(backendDdaChannel, QueryParseStatus.FIELD);
                        break;
                    }
                    ResultSetHeaderPacket header = new ResultSetHeaderPacket();
                    header.decode(byteBuffer);
                    this.filedCount = header.fieldCount;
                    this.headerPacket = header;
                    this.fieldPackets = new ArrayList<MysqlPacket>(filedCount);
                    queryParseStatusMapping.put(backendDdaChannel, QueryParseStatus.FIELD);
                    this.overallQueryParseStatus = QueryParseStatus.FIELD;
                    break;
                //解析完了header需要看Field 要不是没有过，要么是下一个
                case FIELD:
                    if (b == ErrorPacket.FIELD_COUNT) {
                        logger.error("解析resuletset field 出错");
                        break;
                    }
                    if (b == EOFPacket.FIELD_COUNT) {
                        this.filedEofPacket = binaryPacket;
                        queryParseStatusMapping.put(backendDdaChannel, QueryParseStatus.ROWDATA);
                        if (overallQueryParseStatus != QueryParseStatus.ROWDATA) {
                            this.overallQueryParseStatus = QueryParseStatus.ROWDATA;
                        }
                        break;
                    }
                    //需要解析成FieldPacket
                    FieldPacket fieldPacket = new FieldPacket();
                    fieldPacket.decode(byteBuffer);
                    //不在field列表中 则直接添加
                    if (!fieldPackets.contains(fieldPacket)) {
                        fieldPackets.add(fieldPacket);
                    }
                    break;
                case ROWDATA:
                    if (b == ErrorPacket.FIELD_COUNT) {
                        logger.error("继续解析FIELD出错");
                        break;
                    }
                    if (b == EOFPacket.FIELD_COUNT) {
                        this.rowdataEndPacket = binaryPacket;
                        queryParseStatusMapping.put(backendDdaChannel, QueryParseStatus.END);
                        this.nodeExecutor.countDown();
                        break;
                    }
                    this.rowDataPackets.add(binaryPacket);
                    break;
                default:
                    break;
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void encodeForFront(FrontDdaChannel frontDdaChannel) {
        frontDdaChannel.write(headerPacket);
        for (MysqlPacket fieldPacket : fieldPackets) {
            frontDdaChannel.write(fieldPacket);
        }
        frontDdaChannel.write(filedEofPacket);
        for (MysqlPacket rowDataPacket : rowDataPackets) {
            frontDdaChannel.write(rowDataPacket);
        }
        frontDdaChannel.write(rowdataEndPacket);
    }

}
