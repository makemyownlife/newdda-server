package com.elong.pb.newdda.common;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertFalse;

/**
 * ID生成器的测试类
 * Created by zhangyong on 15/8/24.
 */
public class IdWorkerUnitTest {

    private IdWorker idWorker1, idWorker2;

    @Before
    public void setUp() {
        this.idWorker1 = new IdWorker(20);
        this.idWorker2 = new IdWorker(21);
    }

    @Test
    public void testNextIdThree() {
        final long id1 = this.idWorker1.nextId();
        final long id2 = this.idWorker1.nextId();
        final long id3 = this.idWorker1.nextId();

        System.out.println(id1 + " " + id2 + " " + id3);
        assertFalse(id1 == id2);
        assertFalse(id1 == id3);
        assertFalse(id2 == id3);
    }

    @Test
    public void testNextIdTwoWorkers() {
        final long id1 = this.idWorker1.nextId();
        final long id2 = this.idWorker2.nextId();
        final long id3 = this.idWorker1.nextId();
        final long id4 = this.idWorker2.nextId();

        assertFalse(id1 == id2);
        assertFalse(id1 == id3);
        assertFalse(id2 == id3);
        assertFalse(id1 == id4);
        assertFalse(id2 == id4);
        assertFalse(id3 == id4);
        assertFalse(id2 == id4);
    }
}
