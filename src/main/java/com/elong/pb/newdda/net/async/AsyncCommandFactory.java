package com.elong.pb.newdda.net.async;

import com.elong.pb.newdda.net.NodeExecutor;
import com.elong.pb.newdda.parser.ServerParse;

/**
 * Created by zhangyong on 15/4/5.
 */
public class AsyncCommandFactory {

    public static AsyncCommand createAsyncCommand(NodeExecutor executor, String sql) {
        int rs = ServerParse.parse(sql) & 0xff;
        //读操作
        if (rs == 7 || rs == 9) {
            return new QueryAsyncCommand(executor);
        }
        //写操作  4 是insert
        if (rs == 8 || rs == 4) {
            return new UpdateAsyncCommand(executor);
        }
        return null;
    }

}
