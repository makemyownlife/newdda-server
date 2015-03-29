package com.elong.pb.newdda.net;

import com.elong.pb.newdda.route.RouteResultSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多数据节点执行器
 */
public final class MultiNodeExecutor extends NodeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MultiNodeExecutor.class);

    private static final int RECEIVE_CHUNK_SIZE = 16 * 1024;

    private AtomicBoolean isFail = new AtomicBoolean(false);

    private int unfinishedNodeCount;

    private int errno;

    private String errMessage;

    private boolean fieldEOF;

    private byte packetId;

    private long affectedRows;

    private long insertId;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition taskFinished = lock.newCondition();

    public void execute(RouteResultSetNode[] nodes, FrontBackendSession session, String sql) {
        //初始化(相关的参数)
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.isFail.set(false);
            this.unfinishedNodeCount = nodes.length;
            this.errno = 0;
            this.errMessage = null;
            this.fieldEOF = false;
            this.packetId = 0;
            this.affectedRows = 0L;
            this.insertId = 0L;
        } finally {
            lock.unlock();
        }

        //分析所有的链接 并且将所有的链接设置为
        ConcurrentHashMap<RouteResultSetNode, BackendDdaChannel> target = session.getTarget();
        for (RouteResultSetNode rrn : nodes) {
            BackendDdaChannel backendDdaChannel = target.get(rrn);
            if (backendDdaChannel != null) {
                backendDdaChannel.setRunning(true);
            }
        }

    }

    @Override
    public void terminate() throws InterruptedException {

    }

}
