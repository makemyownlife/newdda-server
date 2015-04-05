package com.elong.pb.newdda.net.async;

import com.elong.pb.newdda.net.BackendDdaChannel;
import com.elong.pb.newdda.net.FrontDdaChannel;
import com.elong.pb.newdda.net.NodeExecutor;
import com.elong.pb.newdda.packet.BinaryPacket;
import com.elong.pb.newdda.packet.MysqlPacket;
import com.elong.pb.newdda.packet.OkPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyong on 15/4/4.
 */
public class UpdateAsyncCommand implements AsyncCommand {

    private final static Logger logger = LoggerFactory.getLogger(UpdateAsyncCommand.class);

    private final Lock lock = new ReentrantLock();

    private long affectedRows;

    private long insertId;

    private OkPacket okPacket;

    private BinaryPacket binaryPacket;

    private NodeExecutor executor;

    public UpdateAsyncCommand(NodeExecutor executor) {
        this.executor = executor;
        this.insertId = 0;
    }

    @Override
    public void asyncMysqlPacket(BackendDdaChannel backendDdaChannel, MysqlPacket mysqlPacket) {
        BinaryPacket binaryPacket = (BinaryPacket) mysqlPacket;
//        try {
//            lock.lock();
//            OkPacket temp = new OkPacket();
//            temp.decode(binaryPacket.getByteBuffer());
//            //初始化
//            if (okPacket == null) {
//                okPacket = temp;
//                if (temp.insertId > 0) {
//                    this.insertId = okPacket.insertId;
//                }
//                this.affectedRows = okPacket.affectedRows;
//            } else {
//                this.affectedRows += temp.affectedRows;
//                this.insertId = Math.min(this.insertId, temp.insertId);
//            }
//            executor.countDown();
//        } finally {
//            lock.unlock();
//        }
        this.binaryPacket =  binaryPacket;
        executor.countDown();
    }

    @Override
    public void encodeForFront(FrontDdaChannel frontDdaChannel) {
        frontDdaChannel.write(binaryPacket);
    }

}
