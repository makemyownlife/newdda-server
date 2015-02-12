package com.elong.pb.newdda.net;

import com.elong.pb.newdda.common.RemotingUtil;
import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.server.BackendClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangyong on 15/2/8.
 * 后端 每个location的池子
 */
public class BackendChannelPool {

    private final static Logger logger = LoggerFactory.getLogger(BackendChannelPool.class);

    private final ReentrantLock lock = new ReentrantLock();

    private final DataSourceConfig dataSourceConfig;

    private final DataSourceLocation dataSourceLocation;

    private final BackendDdaChannel[] items;

    private final int size;

    //已经在使用的链接
    private int activeCount;

    //空闲可用的链接
    private int idleCount;

    public BackendChannelPool(DataSourceConfig dataSourceConfig, DataSourceLocation dataSourceLocation, int size) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSourceLocation = dataSourceLocation;
        this.size = size;
        this.items = new BackendDdaChannel[size];
    }

    public void initMinConn() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int minconn = this.dataSourceConfig.getInitconn();
            for (int i = 0; i < minconn; i++) {
                BackendDdaChannel backendDdaChannel = createBackendDdaChannel();
                if (backendDdaChannel != null) {
                    for (int j = 0; j < items.length; j++) {
                        if (items[j] == null) {
                            items[j] = backendDdaChannel;
                            idleCount++;
                            break;
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public BackendDdaChannel getBackendDdaChannel() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            // too many active connections 直接返回无法取得必要的链接
            if (activeCount >= size) {
                StringBuilder s = new StringBuilder();
                s.append("[id=").append(dataSourceConfig.getId());
                s.append("[location=").append(dataSourceLocation.getAddress()).append(",active=");
                s.append(activeCount).append(",size=").append(size).append(']');
                logger.error(s.toString());
                return null;
            }

            //遍历 分库上所有的链接
            final BackendDdaChannel[] items = this.items;
            for (int i = 0, len = items.length; idleCount > 0 && i < len; ++i) {
                if (items[i] != null) {
                    BackendDdaChannel backendDdaChannel = items[i];
                    items[i] = null;
                    --idleCount;
                    if (backendDdaChannel.isClosedOrQuit()) {
                        continue;
                    }
                    return backendDdaChannel;
                }
            }

            //若找到不到链接 则直接创建链接
            BackendDdaChannel newBackendDdaChannel = createBackendDdaChannel();
            if (newBackendDdaChannel != null) {
                activeCount++;
            }
            return newBackendDdaChannel;
        } finally {
            lock.unlock();
        }
    }

    //释放链接，并且放入到数组中，以供下次使用
    public void releaseBackendChannel(BackendDdaChannel backendDdaChannel) {
        if (backendDdaChannel == null || backendDdaChannel.isClosedOrQuit()) {
            return;
        }
        // release connection
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final BackendDdaChannel[] items = this.items;
            for (int i = 0; i < items.length; i++) {
                if (items[i] == null) {
                    ++idleCount;
                    --activeCount;
                    items[i] = backendDdaChannel;
                    return;
                }
            }
        } finally {
            lock.unlock();
        }
        //close excess connection 暂时先不加 ，我们以后看是否需要添加
    }

    public void realCloseBackendChannel(BackendDdaChannel backendDdaChannel, boolean isForce) {
        if (backendDdaChannel == null || backendDdaChannel.isClosedOrQuit()) {
            return;
        }
        if (backendDdaChannel.isRunning() && isForce) {
            logger.warn("backendChannel:{} is still running", backendDdaChannel);
            return;
        }
        // release connection
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final BackendDdaChannel[] items = this.items;
            //该链接是否在运行，不在数组池中
            boolean isActive = true;
            for (int i = 0; i < items.length; i++) {
                if (items[i] == backendDdaChannel) {
                    --idleCount;
                    items[i] = null;
                    isActive = false;
                    break;
                }
            }
            //正在活跃，则需要直接减去相关的值
            if (isActive) {
                --activeCount;
            }
            //关闭链接 ，并且去掉相关的映射
            Channel channel = backendDdaChannel.getChannelWrapper().getChannel();
            backendDdaChannel.getIsClosed().compareAndSet(false, true);
            BackendClient.getInstance().removeBackendChannel(channel);
            backendDdaChannel = null;
        } finally {
            lock.unlock();
        }
    }

    public BackendDdaChannel createBackendDdaChannel() {
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

    public DataSourceConfig getDataSourceConfig() {
        return this.dataSourceConfig;
    }

    public DataSourceLocation getDataSourceLocation() {
        return dataSourceLocation;
    }

}
