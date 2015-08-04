package com.elong.pb.newdda.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载中间件的数据库配置
 * Created by zhangyong on 15/8/3.
 */
public class DdaConfigLoader {

    public static final Logger logger = LoggerFactory.getLogger(DdaConfigLoader.class);

    private static DdaConfigLoader INSTANCE = new DdaConfigLoader();

    public static DdaConfigLoader getInstance(){
        return INSTANCE;
    }

    public void init(){

    }

}
