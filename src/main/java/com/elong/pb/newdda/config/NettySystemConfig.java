package com.elong.pb.newdda.config;

/**
 * netty系统配置
 * User: zhangyong
 * Date: 2014/9/6
 * Time: 10:46
 * if you have any question ,please contact zhangyong7120180@163.com
 */
public class NettySystemConfig {

    public static final String SystemPropertyNettyPooledByteBufAllocatorEnable =
            "com.rocketmq.remoting.nettyPooledByteBufAllocatorEnable";

    public static boolean NettyPooledByteBufAllocatorEnable = //
            Boolean
                    .parseBoolean(System.getProperty(SystemPropertyNettyPooledByteBufAllocatorEnable, "false"));

    public static final String SystemPropertySocketSndbufSize = //
            "com.rocketmq.remoting.socket.sndbuf.size";

    public static int SocketSndbufSize = //
            Integer.parseInt(System.getProperty(SystemPropertySocketSndbufSize, "65535"));

    public static final String SystemPropertySocketRcvbufSize = //
            "com.rocketmq.remoting.socket.rcvbuf.size";

    public static int SocketRcvbufSize = //
            Integer.parseInt(System.getProperty(SystemPropertySocketRcvbufSize, "65535"));

    public static final String SystemPropertyClientAsyncSemaphoreValue = //
            "com.rocketmq.remoting.clientAsyncSemaphoreValue";

    public static int ClientAsyncSemaphoreValue = //
            Integer.parseInt(System.getProperty(SystemPropertyClientAsyncSemaphoreValue, "2048"));

    public static final String SystemPropertyClientOnewaySemaphoreValue = //
            "com.rocketmq.remoting.clientOnewaySemaphoreValue";

    public static int ClientOnewaySemaphoreValue = //
            Integer.parseInt(System.getProperty(SystemPropertyClientOnewaySemaphoreValue, "2048"));

}
