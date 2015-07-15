package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端服务
 * Created by zhangyong on 15/7/9.
 */
public class FrontServer {

    private final static Logger logger = LoggerFactory.getLogger(FrontServer.class);

    private static Object mutex = new Object();

    private static FrontServer INSTANCE = null;

    public static FrontServer getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (mutex) {
            INSTANCE = new FrontServer();
            return INSTANCE;
        }
    }

    //启动
    public void start(){
        FrontClient.getInstance().start();
    }

    //关闭
    public void stop(){

    }

}
