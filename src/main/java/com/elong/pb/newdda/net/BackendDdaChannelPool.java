package com.elong.pb.newdda.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 后端 每个location的池子 (当前的配置锁的竞争会相对激烈)
 * User: zhangyong
 * Date: 2016/3/8
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class BackendDdaChannelPool {

    private static final Logger logger = LoggerFactory.getLogger(BackendDdaChannelPool.class);

    private final ReentrantLock lock = new ReentrantLock();

    //池中的最大的连接数
    private int maxSize;

    //正在运行的链接
    private ArrayBlockingQueue activeQueue;

    //空闲的链接
    private ArrayBlockingQueue idleQueue;

    public BackendDdaChannelPool(int maxSize) {
        this.maxSize = maxSize;
        this.activeQueue = new ArrayBlockingQueue(maxSize);
        this.idleQueue = new ArrayBlockingQueue(maxSize);
    }

}

