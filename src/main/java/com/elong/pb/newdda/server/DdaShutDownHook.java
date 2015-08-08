package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关闭的时候，执行的程序 柔和的关闭内容
 * Created by zhangyong on 15/8/4.
 */
public class DdaShutDownHook {

    private final static Logger logger = LoggerFactory.getLogger(DdaShutDownHook.class);

    private static final DdaShutDownHook INSTANCE = new DdaShutDownHook();

    public static DdaShutDownHook getInstance() {
        return INSTANCE;
    }

    //添加关闭信号量时候，进行相关的操作
    public void addShutDownHook() {
        
    }

}
