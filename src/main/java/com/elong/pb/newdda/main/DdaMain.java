package com.elong.pb.newdda.main;

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
        logger.info("开始启动分布式数据库中间件dda ");
        logger.info("结束启动分布式数据库中间件dda 总耗时:{}毫秒", System.currentTimeMillis() - start);
    }

}
