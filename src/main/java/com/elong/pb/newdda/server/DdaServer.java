package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/21.
 * DDA启动server
 */
public class DdaServer {

    private static Logger logger = LoggerFactory.getLogger(DdaServer.class);

    private static DdaServer INSTANCE = new DdaServer();

    public static DdaServer getInstance() {
        return INSTANCE;
    }

    public void start() {
        logger.info("开始启动DDAserver...(⊙o⊙)");

        logger.info("开始启动DDA前端服务");

        logger.info("开始启动DDA后端连mysql服务");
        BackendServer.getInstance().start();

        logger.info("结束启动DDAserver...O(∩_∩)O");
    }

}
