package com.elong.pb.newdda.net;

import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.server.BackendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 后端 每个location的池子 (当前的配置锁的竞争会相对激烈)
 * User: zhangyong
 * Date: 2016/3/8
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class BackendDdaChannelPool {

    private static final Logger logger = LoggerFactory.getLogger(BackendDdaChannelPool.class);

    private final static Long AUTH_TIME_OUT = 20 * 1000L;

    private DataSourceLocation dataSourceLocation;

    //池中的最大的连接数
    private int maxSize;

    //初始化连接数
    private int initSize;

    //正在运行的链接
    private ArrayBlockingQueue<BackendDdaChannel> activeQueue;

    //空闲的链接
    private ArrayBlockingQueue<BackendDdaChannel> idleQueue;

    public BackendDdaChannelPool(int maxSize, int initSize, DataSourceLocation dataSourceLocation) {
        this.maxSize = maxSize;
        this.initSize = initSize;
        this.dataSourceLocation = dataSourceLocation;
        this.activeQueue = new ArrayBlockingQueue<BackendDdaChannel>(maxSize);
        this.idleQueue = new ArrayBlockingQueue<BackendDdaChannel>(maxSize);
    }

    public BackendDdaChannel getBackendChannel() {
        BackendDdaChannel backendDdaChannel = idleQueue.poll();
        if (backendDdaChannel != null) {
            return backendDdaChannel;
        }
        //这里在连接池里判断的 ，若已经超过最大链接数 则直接返回null 若以后需要阻塞等待
        int activeCount = activeQueue.size();
        if (activeCount >= maxSize) {
            return null;
        }
        //若找到不到链接 则直接创建链接
        BackendDdaChannel newBackendDdaChannel = createAuthBackendDdaChannel();
        activeQueue.offer(newBackendDdaChannel);
        return newBackendDdaChannel;
    }

    public void releaseBackendChannel(BackendDdaChannel backendDdaChannel) {

    }

    //========================================================================== private method start =========================================================================================================
    private BackendDdaChannel createBackendDdaChannel() {
        String remoteAddress = this.dataSourceLocation.getAddress();
        ChannelWrapper channelWrapper = null;
        try {
            channelWrapper = BackendClient.getInstance().createChannel(remoteAddress);
        } catch (InterruptedException e) {
            logger.warn("createBackendDdaChannel warn:" + e.getMessage());
        }
        if (channelWrapper != null) {
            BackendDdaChannel backendDdaChannel = new BackendDdaChannel(channelWrapper, this);
            BackendClient.getInstance().addBackendChannelMapping(channelWrapper.getChannel(), backendDdaChannel);
            return backendDdaChannel;
        }
        return null;
    }

    private BackendDdaChannel createAuthBackendDdaChannel() {
        BackendDdaChannel backendDdaChannel = createBackendDdaChannel();
        if (backendDdaChannel == null) {
            return null;
        }
        boolean ready = false;
        try {
            ready = backendDdaChannel.getBackendAuthHandler().getCountDownLatch().await(AUTH_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
        if (!ready) {
            logger.error("fail to auth backendchannel:{} reason: time out", backendDdaChannel);
            return null;
        }
        return backendDdaChannel;
    }

    //========================================================================== private method end =========================================================================================================


}

