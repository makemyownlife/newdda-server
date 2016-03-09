package com.elong.pb.newdda.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final BackendDdaChannel[] items;

    private final int maxSize;

    //已经在使用的链接
    private int activeCount;

    //空闲可用的链接
    private int idleCount;

    public BackendDdaChannelPool(int maxSize) {
        this.maxSize = maxSize;
        this.items = new BackendDdaChannel[maxSize];
    }

}

