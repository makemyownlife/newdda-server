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
package com.elong.pb.newdda.packet;

import com.elong.pb.newdda.common.BufferUtil;

import java.nio.ByteBuffer;

/**
 * From Server To Client, part of Result Set Packets. One for each column in the
 * result set. Thus, if the value of field_columns in the Result Set Header
 * Packet is 3, then the Field Packet occurs 3 times.
 * <p/>
 * <pre>
 * Bytes                      Name
 * -----                      ----
 * n (Length Coded String)    catalog
 * n (Length Coded String)    db
 * n (Length Coded String)    table
 * n (Length Coded String)    org_table
 * n (Length Coded String)    name
 * n (Length Coded String)    org_name
 * 1                          (filler)
 * 2                          charsetNumber
 * 4                          length
 * 1                          type
 * 2                          flags
 * 1                          decimals
 * 2                          (filler), always 0x00
 * n (Length Coded Binary)    default
 *
 * @author xianmao.hexm 2010-7-22 下午05:43:34
 * @see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Field_Packet
 * </pre>
 */
public class FieldPacket extends MysqlPacket implements Packet {

    private static final byte[] DEFAULT_CATALOG = "def".getBytes();

    private static final byte[] FILLER = new byte[2];

    public byte[] catalog = DEFAULT_CATALOG;
    public byte[] db;
    public byte[] table;
    public byte[] orgTable;
    public byte[] name;
    public byte[] orgName;
    public int charsetIndex;
    public long length;
    public int type;
    public int flags;
    public byte decimals;
    public byte[] definition;

    @Override
    public int calcPacketSize() {
        int size = (catalog == null ? 1 : BufferUtil.getLength(catalog));
        size += (db == null ? 1 : BufferUtil.getLength(db));
        size += (table == null ? 1 : BufferUtil.getLength(table));
        size += (orgTable == null ? 1 : BufferUtil.getLength(orgTable));
        size += (name == null ? 1 : BufferUtil.getLength(name));
        size += (orgName == null ? 1 : BufferUtil.getLength(orgName));
        size += 13;// 1+2+4+1+2+1+2
        if (definition != null) {
            size += BufferUtil.getLength(definition);
        }
        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Field Packet";
    }

    @Override
    public boolean decode(ByteBuffer byteBuffer) {
        this.packetLength = BufferUtil.readUB3(byteBuffer);
        this.packetId = byteBuffer.get();
        this.catalog = BufferUtil.readBytesWithLength(byteBuffer);
        this.db = BufferUtil.readBytesWithLength(byteBuffer);
        this.table = BufferUtil.readBytesWithLength(byteBuffer);
        this.orgTable = BufferUtil.readBytesWithLength(byteBuffer);
        this.name = BufferUtil.readBytesWithLength(byteBuffer);
        this.orgName = BufferUtil.readBytesWithLength(byteBuffer);
        BufferUtil.stepBuffer(byteBuffer, 1);
        this.charsetIndex = BufferUtil.readUB2(byteBuffer);
        this.length = BufferUtil.readUB4(byteBuffer);
        this.type = byteBuffer.get() & 0xff;
        this.flags = BufferUtil.readUB2(byteBuffer);
        this.decimals = byteBuffer.get();
        BufferUtil.stepBuffer(byteBuffer, FILLER.length);
        if (byteBuffer.hasRemaining()) {
            this.definition = BufferUtil.readBytesWithLength(byteBuffer);
        }
        return true;
    }


    private void writeBody(ByteBuffer buffer) {
        byte nullVal = 0;
        BufferUtil.writeWithLength(buffer, catalog, nullVal);
        BufferUtil.writeWithLength(buffer, db, nullVal);
        BufferUtil.writeWithLength(buffer, table, nullVal);
        BufferUtil.writeWithLength(buffer, orgTable, nullVal);
        BufferUtil.writeWithLength(buffer, name, nullVal);
        BufferUtil.writeWithLength(buffer, orgName, nullVal);
        buffer.put((byte) 0x0C);
        BufferUtil.writeUB2(buffer, charsetIndex);
        BufferUtil.writeUB4(buffer, length);
        buffer.put((byte) (type & 0xff));
        BufferUtil.writeUB2(buffer, flags);
        buffer.put(decimals);
        buffer.position(buffer.position() + FILLER.length);
        if (definition != null) {
            BufferUtil.writeWithLength(buffer, definition);
        }
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(
                3 + 1 + calcPacketSize()
        );
        BufferUtil.writeUB3(buffer, calcPacketSize());
        buffer.put(packetId);
        writeBody(buffer);
        buffer.flip();
        return buffer;
    }

    public boolean equals(Object object) {
        if (!(object instanceof FieldPacket)) {
            return false;
        }
        FieldPacket temp = (FieldPacket) object;
        //比较name table
        if (name != null && temp.name != null) {
            if (name.length != temp.name.length) {
                return false;
            }
            for (int i = 0; i < name.length; i++) {
                if (name[i] != temp.name[i]) {
                    return false;
                }
            }
        }
        //比较table
        if (table != null && temp.table != null) {
            if (table.length != temp.table.length) {
                return false;
            }
            for (int i = 0; i < table.length; i++) {
                if (table[i] != temp.table[i]) {
                    return false;
                }
            }
        }
        //比较orgName
        if (orgName != null && temp.orgName != null) {
            if (orgName.length != temp.orgName.length) {
                return false;
            }
            for (int i = 0; i < orgName.length; i++) {
                if (orgName[i] != temp.orgName[i]) {
                    return false;
                }
            }
        }
        return true;
    }

}
