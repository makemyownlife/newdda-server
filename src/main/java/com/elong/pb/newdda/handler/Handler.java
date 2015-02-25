package com.elong.pb.newdda.handler;

import java.nio.ByteBuffer;

/**
 * Created by zhangyong on 15/2/13.
 */
public interface Handler {

    public void handle(ByteBuffer byteBuffer) throws Exception;

}
