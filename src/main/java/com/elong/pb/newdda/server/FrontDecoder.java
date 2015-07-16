package com.elong.pb.newdda.server;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

/**
 * 前端解码器(重写)
 * Created by zhangyong on 15/7/15.
 */
public class FrontDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger logger = LoggerFactory.getLogger(FrontDecoder.class);

    private static final Integer MAX_PACKET_SIZE = 1024 * 1024 * 16;

    public FrontDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_SIZE, 0, 3, 1, 0, true);
    }



}
