package com.elong.pb.newdda.config;

/**
 * netty系统配置
 * User: zhangyong
 * Date: 2014/9/6
 * Time: 10:46
 * if you have any question ,please contact zhangyong7120180@163.com
 */
public class NettySystemConfig {

    public static int SocketSndbufSize = 65535;

    public static int SocketRcvbufSize = 65535;

    public static int ClientAsyncSemaphoreValue = 2048;

    public static int ClientOnewaySemaphoreValue = 2048;

}
