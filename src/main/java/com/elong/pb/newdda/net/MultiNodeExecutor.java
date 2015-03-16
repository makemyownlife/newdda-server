package com.elong.pb.newdda.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void terminate() throws InterruptedException {

    }

}
