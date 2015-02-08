package com.elong.pb.newdda.net;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.server.BackendClient;
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

    private int activeCount;

    private int idleCount;

    public BackendChannelPool(DataSourceConfig dataSourceConfig, DataSourceLocation dataSourceLocation, int size) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSourceLocation = dataSourceLocation;
        this.size = size;
        this.items = new BackendDdaChannel[size];
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
                
            }

        } finally {
            lock.unlock();
        }
        return null;
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
            BackendDdaChannel backendDdaChannel = new BackendDdaChannel(channelWrapper);
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
