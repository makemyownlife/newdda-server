package com.elong.pb.newdda.net;

/**
 * Created by zhangyong on 15/2/8.
 * dda后端连接 （也就是mysql链接）
 */
public class BackendDdaChannel implements DdaChannel {

    private static ConnectIdGenerator CONNECT_ID_GENERATOR = new ConnectIdGenerator();

    private Long id;

    public BackendDdaChannel() {
        this.id = CONNECT_ID_GENERATOR.getId();
    }

    //后端ID生成器
    private static class ConnectIdGenerator {
        private static final long MAX_VALUE = Long.MAX_VALUE;
        private long connectId = 0L;
        private final Object lock = new Object();

        private long getId() {
            synchronized (lock) {
                if (connectId >= MAX_VALUE) {
                    connectId = 0L;
                }
                return ++connectId;
            }
        }
    }

}
