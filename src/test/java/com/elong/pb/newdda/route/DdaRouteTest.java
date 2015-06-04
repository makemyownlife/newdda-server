package com.elong.pb.newdda.route;

import com.alibaba.cobar.parser.ast.stmt.SQLStatement;
import com.alibaba.cobar.parser.recognizer.SQLParserDelegate;
import com.alibaba.cobar.parser.recognizer.mysql.syntax.MySQLParser;
import com.elong.pb.newdda.config.DataSourceConfig;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 15/5/31.
 * dda 相关的路由 测试
 */
public class DdaRouteTest {

    private static final String charset = "UTF-8";

    @Test
    public void routeTest() throws Exception {
        String stmt = "select * from user a  where a.card_no = '420116198401082811' ";
        SQLStatement ast = SQLParserDelegate.parse(stmt, charset == null ? MySQLParser.DEFAULT_CHARSET : charset);

        DataSourceConfig config = new DataSourceConfig();
        config.setKeyname("card_no");
        PartitionKeyVisitor visitor = new PartitionKeyVisitor(config);
        ast.accept(visitor);

        //判断列的值
        Map<String, Map<String, List<Object>>> astExt = visitor.getColumnValue();
        Map<String, List<Object>> currentCol2Val = null;
        for (Map.Entry<String, Map<String, List<Object>>> e : astExt.entrySet()) {
            Map<String, List<Object>> col2Val = e.getValue();
            if (col2Val == null || col2Val.isEmpty()) {
                continue;
            }
            currentCol2Val = col2Val;
        }

        //匹配规则
        boolean matchRule = false;
        if (currentCol2Val != null) {
            List<Object> list = currentCol2Val.get(config.getKeyname().toUpperCase());
        }
    }

}
