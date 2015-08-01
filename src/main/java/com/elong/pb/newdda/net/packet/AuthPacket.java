package com.elong.pb.newdda.net.packet;

import com.elong.pb.newdda.common.BufferUtil;
import com.elong.pb.newdda.config.Capabilities;

import java.nio.ByteBuffer;

/**
 *
 * From client to server during initial handshake.
 *
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 4                            client_flags
 * 4                            max_packet_size
 * 1                            charset_number
 * 23                           (filler) always 0x00...
 * n (Null-Terminated String)   user
 * n (Length Coded Binary)      scramble_buff (1 + x bytes)
 * n (Null-Terminated String)   databasename (optional)
 *
 * @see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Client_Authentication_Packet
 * </pre>
 *
 * @author xianmao.hexm 2010-7-15 下午04:35:34
 *
 * 握手后 client 发送验证包
 * Created by zhangyong on 15/8/1.
 */
public class AuthPacket extends MysqlPacket implements Packet {

    private static final byte[] FILLER = new byte[23];

    private long clientFlags;

    private long maxPacketSize;

    private int charsetIndex;

    private byte[] extra;//from FILLER(23)

    private String user;

    private byte[] password;

    private String database;

    @Override
    public int calcPacketSize() {
        int size = 32;// 4+4+1+23;
        size += (user == null) ? 1 : user.length() + 1;
        size += (password == null) ? 1 : BufferUtil.getLength(password);
        size += (database == null) ? 1 : database.length() + 1;
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "AuthPackt";
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId = byteBuffer.get();
        this.clientFlags = BufferUtil.readUB4(byteBuffer);
        this.maxPacketSize = BufferUtil.readUB4(byteBuffer);
        this.charsetIndex = (byteBuffer.get() & 0xff);

        int current = byteBuffer.position();
        //read extra
        int len = (int) BufferUtil.readLength(byteBuffer);
        if (len > 0 && len < FILLER.length) {
            byte[] ab = new byte[len];
            byteBuffer.get(ab, current, len);
        }

        byteBuffer.position(current + FILLER.length);
        this.user = BufferUtil.readTerminateStringWithNull(byteBuffer);

        //因为最后一个是 byte 0 所以必须skip掉
        byteBuffer.get();
        this.password = BufferUtil.readBytesWithLength(byteBuffer);

        if (((clientFlags & Capabilities.CLIENT_CONNECT_WITH_DB) != 0) && byteBuffer.hasRemaining()) {
            database = BufferUtil.readTerminateStringWithNull(byteBuffer);
        }

        return true;
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
        BufferUtil.writeUB4(buffer, clientFlags);
        BufferUtil.writeUB4(buffer, maxPacketSize);
        buffer.put((byte) charsetIndex);
        buffer.put(FILLER);
        if (user == null) {
            buffer.put((byte) 0);
        } else {
            byte[] userData = user.getBytes();
            BufferUtil.writeWithNull(buffer, userData);
        }
        if (password == null) {
            buffer.put((byte) 0);
        } else {
            BufferUtil.writeWithLength(buffer, password);
        }
        if (database == null) {
            buffer.put((byte) 0);
        } else {
            byte[] databaseData = database.getBytes();
            BufferUtil.writeWithNull(buffer, databaseData);
        }
        buffer.flip();
        return buffer;
    }

    //================================================= set get method =============================================
    public long getClientFlags() {
        return clientFlags;
    }

    public void setClientFlags(long clientFlags) {
        this.clientFlags = clientFlags;
    }

    public long getMaxPacketSize() {
        return maxPacketSize;
    }

    public void setMaxPacketSize(long maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    public int getCharsetIndex() {
        return charsetIndex;
    }

    public void setCharsetIndex(int charsetIndex) {
        this.charsetIndex = charsetIndex;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setExtra(byte[] extra) {
        this.extra = extra;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
