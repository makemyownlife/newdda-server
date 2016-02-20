package com.elong.pb.newdda.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NameableExecutor extends ThreadPoolExecutor {

    protected String name;

    public NameableExecutor(String name, int coreSize, int maxSize, BlockingQueue<Runnable> queue, ThreadFactory factory) {
        super(coreSize, maxSize, Long.MAX_VALUE, TimeUnit.NANOSECONDS, queue, factory);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
