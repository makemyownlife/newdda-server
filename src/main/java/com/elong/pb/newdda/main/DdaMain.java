package com.elong.pb.newdda.main;

import com.elong.pb.newdda.server.DdaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DDA 分布式数据库中间件的
 * Created by zhangyong on 15/6/29.
 */
public class DdaMain {

    private static Logger logger = LoggerFactory.getLogger(DdaMain.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            logger.info("开始启动分布式数据库中间件dda ");
            DdaServer.getInstance().start();
            logger.info("结束启动分布式数据库中间件dda 总耗时:{}毫秒", System.currentTimeMillis() - start);
        } catch (Throwable e) {
            logger.error("main start error: ", e);
            System.exit(-1);
        }
    }

}
