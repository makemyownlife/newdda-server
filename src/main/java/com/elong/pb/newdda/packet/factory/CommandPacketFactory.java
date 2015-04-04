package com.elong.pb.newdda.packet.factory;

import com.elong.pb.newdda.packet.CommandPacket;
import com.elong.pb.newdda.packet.MysqlPacket;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhangyong on 14/12/29.
 * 命令包的创建
 */
public class CommandPacketFactory {

    private static String DEFAULT_SQL_MODE = "STRICT_TRANS_TABLES";

    public static CommandPacket createSqlModeCommand() {
        StringBuilder s = new StringBuilder();
        s.append("SET sql_mode=\"").append(DEFAULT_SQL_MODE).append('"');
        CommandPacket cmd = new CommandPacket();
        cmd.packetId = 0;
        cmd.command = MysqlPacket.COM_QUERY;
        cmd.arg = s.toString().getBytes();
        return cmd;
    }

    public static CommandPacket createQueryCommand(String statement ,String charset) throws UnsupportedEncodingException {
        CommandPacket command = new CommandPacket();
        command.packetId = 0 ;
        command.command = MysqlPacket.COM_QUERY;
        command.arg = statement.getBytes(charset);
        return command;
    }

}
