package com.elong.pb.newdda.config;

/**
 * Created by zhangyong on 14/12/15.
 */
public class NettyClientConfig {

    private int clientWorkerThreads = 4;

    private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();

    private int clientOnewaySemaphoreValue = NettySystemConfig.ClientOnewaySemaphoreValue;

    private int clientAsyncSemaphoreValue = NettySystemConfig.ClientAsyncSemaphoreValue;

    private long connectTimeoutMillis = 3000;
    // channel超过1分钟不被访问 就关闭

    private long channelNotActiveInterval = 1000 * 60;

    private int clientChannelMaxIdleTimeSeconds = 120;

    private int clientSocketSndBufSize = NettySystemConfig.SocketSndbufSize;

    private int clientSocketRcvBufSize = NettySystemConfig.SocketRcvbufSize;

    private boolean clientPooledByteBufAllocatorEnable = false;

    public static final long MAX_PACKET_SIZE = 1024 * 1024 * 16;

    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }


    public void setClientWorkerThreads(int clientWorkerThreads) {
        this.clientWorkerThreads = clientWorkerThreads;
    }


    public int getClientOnewaySemaphoreValue() {
        return clientOnewaySemaphoreValue;
    }


    public void setClientOnewaySemaphoreValue(int clientOnewaySemaphoreValue) {
        this.clientOnewaySemaphoreValue = clientOnewaySemaphoreValue;
    }


    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }


    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }


    public int getClientCallbackExecutorThreads() {
        return clientCallbackExecutorThreads;
    }


    public void setClientCallbackExecutorThreads(int clientCallbackExecutorThreads) {
        this.clientCallbackExecutorThreads = clientCallbackExecutorThreads;
    }


    public long getChannelNotActiveInterval() {
        return channelNotActiveInterval;
    }


    public void setChannelNotActiveInterval(long channelNotActiveInterval) {
        this.channelNotActiveInterval = channelNotActiveInterval;
    }


    public int getClientAsyncSemaphoreValue() {
        return clientAsyncSemaphoreValue;
    }


    public void setClientAsyncSemaphoreValue(int clientAsyncSemaphoreValue) {
        this.clientAsyncSemaphoreValue = clientAsyncSemaphoreValue;
    }


    public int getClientChannelMaxIdleTimeSeconds() {
        return clientChannelMaxIdleTimeSeconds;
    }


    public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
        this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
    }


    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }


    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }


    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }


    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }


    public boolean isClientPooledByteBufAllocatorEnable() {
        return clientPooledByteBufAllocatorEnable;
    }


    public void setClientPooledByteBufAllocatorEnable(boolean clientPooledByteBufAllocatorEnable) {
        this.clientPooledByteBufAllocatorEnable = clientPooledByteBufAllocatorEnable;
    }

}
