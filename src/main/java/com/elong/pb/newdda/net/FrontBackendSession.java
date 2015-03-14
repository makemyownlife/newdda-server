package com.elong.pb.newdda.net;

import com.elong.pb.newdda.route.DdaRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 15/2/11.
 * 前后端一次会话
 * 其实是在翻译cobar 按照我的方式来做，希望做一个尝试。
 */
public class FrontBackendSession {

    private static Logger logger = LoggerFactory.getLogger(FrontBackendSession.class);

    //前端链接
    private FrontDdaChannel frontDdaChannel;

    public FrontBackendSession(FrontDdaChannel frontDdaChannel) {
        this.frontDdaChannel = frontDdaChannel;
    }

    public void execute(String sql) {
        DdaRoute.route(sql, frontDdaChannel.getDataSource());
    }

}
