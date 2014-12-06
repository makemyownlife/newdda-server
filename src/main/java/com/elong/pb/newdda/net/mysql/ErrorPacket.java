/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elong.pb.newdda.net.mysql;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * From server to client in response to command, if error.
 * <p/>
 * <pre>
 * Bytes                       Name
 * -----                       ----
 * 1                           field_count, always = 0xff
 * 2                           errno
 * 1                           (sqlstate marker), always '#'
 * 5                           sqlstate (5 characters)
 * n                           message
 *
 * @author xianmao.hexm 2010-7-16 上午10:45:01
 * @see
 * </pre>
 */
public class ErrorPacket extends MysqlPacket implements Packet {

    public static final byte FIELD_COUNT = (byte) 0xff;

    private static final byte SQLSTATE_MARKER = (byte) '#';

    private static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();

    public byte fieldCount = FIELD_COUNT;

    public int errno;

    public String msg;

    public byte mark = SQLSTATE_MARKER;

    public byte[] sqlState = DEFAULT_SQLSTATE;

    @Override
    public int calcPacketSize() {
        int size = 9;// 1 + 2 + 1 + 5
        if (msg != null) {
            size += msg.getBytes().length;
        }
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Error Packet";
    }

    @Deprecated
    public boolean decode(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(
               3
               + 1
               + calcPacketSize()
        );
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        buffer.put(packetId);
        BufferUtil.writeUB2(buffer, errno);
        buffer.put(mark);
        buffer.put(sqlState);
        if (msg != null) {
            buffer.put(msg.getBytes());
        }
        buffer.flip();
        return buffer;
    }

}
