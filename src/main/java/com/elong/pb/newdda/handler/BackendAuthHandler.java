package com.elong.pb.newdda.handler;

import com.elong.pb.newdda.net.BackendDdaChannel;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * 后端链接验证 ,验证成功后才可以放到 可用连接池中
 * User: zhangyong
 * Date: 2016/3/13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class BackendAuthHandler implements Handler {

    private BackendDdaChannel backendDdaChannel;

    private CountDownLatch countDownLatch;

    private Semaphore semaphore;

    public BackendAuthHandler(BackendDdaChannel backendDdaChannel) {
        this.backendDdaChannel = backendDdaChannel;
        this.countDownLatch = new CountDownLatch(1);
        this.semaphore = new Semaphore(1);
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    @Override
    public void handle(ByteBuffer byteBuffer) throws Exception {


    }

}
