package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

/**
 * Created by zhangyong on 15/1/11.
 * 字节测试
 */
public class ByteTest {

    private static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        //低字节在高字节位
        bytes[0] = (byte) ((i >> 24) & 0xff);
        bytes[1] = (byte) ((i >> 16) & 0xff);
        bytes[2] = (byte) ((i >> 8) & 0xff);
        bytes[3] = (byte) (i & 0xff);
        return bytes;
    }

    private static int bytesToInt(byte[] bytes) {
        int i = 0;
        i |= (bytes[0] & 0xff) << 24;
        i |= (bytes[1] & 0xff) << 16;
        i |= (bytes[2] & 0xff) << 8;
        i |= (bytes[3]) & 0xff;
        return i;
    }

    @Test
    //整形字节想过户转换
    public void testIntToBytes() {
        byte[] bytes = intToBytes(-2);
        System.out.println(bytesToInt(bytes));
    }

    //测试符号位移动 有符号位 无符号位
    @Test
    public void testSign() {
        int negative = 0xff000000;
        //在右移时不保留符号的出来
        int temp = negative >> 8;
        int temp2 = negative >>> 8 ;
        //小于 0
        System.out.println(temp);
        //大于 0
        System.out.println(temp2);
    }

}
