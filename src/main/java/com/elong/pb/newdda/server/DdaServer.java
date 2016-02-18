package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动server
 * Created by zhangyong on 15/7/5.
 */
public class DdaServer {

    private static final Logger logger = LoggerFactory.getLogger(DdaServer.class);

    private static DdaServer INSTANCE = null;

    private static Object mutex = new Object();

    public static DdaServer getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (mutex) {
            INSTANCE = new DdaServer();
            return INSTANCE;
        }
    }

    public void start() {
        DdaConfigLoader.getInstance().init();
        FrontServer.getInstance().start();
        BackendServer.getInstance().start();
        DdaShutDownHook.getInstance().addShutDownHook();
    }


}
