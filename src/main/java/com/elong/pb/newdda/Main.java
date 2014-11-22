package com.elong.pb.newdda;

import com.elong.pb.newdda.server.DdaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 14/11/21.
 * 张勇的新DDA的服务 用于关键字分库
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            DdaServer server = DdaServer.getInstance();
            server.start();
        } catch (Throwable e) {
            logger.error("main start error: ", e);
            System.exit(-1);
        }
    }

}
