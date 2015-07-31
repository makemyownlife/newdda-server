package com.elong.pb.newdda.net;

import io.netty.channel.Channel;

/**
 * 简单的模型
 * Created by zhangyong on 15/7/23.
 */
public abstract class DdaChannel {

    public abstract void write(Object message);

}
