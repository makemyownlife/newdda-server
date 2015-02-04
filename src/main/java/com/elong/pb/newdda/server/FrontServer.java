package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/2/2.
 * 前端链接 jdbc 链接
 */
public class FrontServer {

    private final static Logger logger = LoggerFactory.getLogger(FrontServer.class);

    public static FrontServer INSTANCE = new FrontServer();

    public static FrontServer getInstance() {
        return INSTANCE;
    }

    public void start(){
        
    }

}
