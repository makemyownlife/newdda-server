package com.elong.pb.newdda.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前后端一次会话
 * Created by zhangyong on 15/8/19.
 */
public class FrontBackendSession {

    private final static Logger logger = LoggerFactory.getLogger(FrontBackendSession.class);

    private FrontDdaChannel frontDdaChannel;

    public FrontBackendSession(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
    }

    //执行sql
    public void execute(String sql){

    }

    //释放相关的资源
    public void release() {

    }

}
