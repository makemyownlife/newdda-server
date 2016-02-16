package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.SchemaConfig;
import com.elong.pb.newdda.config.TableConfig;
import com.elong.pb.newdda.config.rule.RuleConfig;
import com.elong.pb.newdda.config.rule.TableRuleConfig;
import com.elong.pb.newdda.parser.ast.stmt.SQLStatement;
import com.elong.pb.newdda.parser.recognizer.SQLParserDelegate;
import com.elong.pb.newdda.parser.recognizer.mysql.syntax.MySQLParser;
import com.elong.pb.newdda.route.visitor.PartitionKeyVisitor;
import com.elong.pb.newdda.server.DdaConfigSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Map;

/**
 * dda路由工具类
 * Created by zhangyong on 15/8/25.
 */
public class DdaRoute {

    private static final Logger logger = LoggerFactory.getLogger(DdaRoute.class);

    public static RouteResultSet route(String sql, String schemaId) throws SQLSyntaxErrorException {
        //举例：sql = "select *  from user where id = 2 and name = 'zhangyong' " 若以id为分区关键字;

        sql = "select *  from user where id = 2 and name = 'zhangyong' ";

        DdaConfigSingleton ddaConfig = DdaConfigSingleton.getInstance();
        Map<String, SchemaConfig> schemas = ddaConfig.getSchemas();
        //是否包含(数据源编号)
        if (!schemas.containsKey(schemaId)) {
            logger.error("cant find schemaId:{} from schema.xml", schemaId);
            return null;
        }

        //涉及到的所有的节点
        SchemaConfig schemaConfig = schemas.get(schemaId);

        //生成和展开AST
        SQLStatement ast = SQLParserDelegate.parse(sql, MySQLParser.DEFAULT_CHARSET);
        PartitionKeyVisitor visitor = new PartitionKeyVisitor(schemaConfig.getTables());
        visitor.setTrimSchema(schemaConfig.isKeepSqlSchema() ? schemaConfig.getName() : null);
        ast.accept(visitor);

        //匹配规则
        TableConfig matchedTable = null;
        RuleConfig rule = null;
        Map<String, List<Object>> columnValues = null;
        Map<String, Map<String, List<Object>>> astExt = visitor.getColumnValue();
        Map<String, TableConfig> tables = schemaConfig.getTables();

        boolean outerBreak = false;
        for (Map.Entry<String, Map<String, List<Object>>> e : astExt.entrySet()) {
            Map<String, List<Object>> col2Val = e.getValue();
            TableConfig tc = tables.get(e.getKey());
            if (tc == null) {
                continue;
            }
            if (matchedTable == null) {
                matchedTable = tc;
            }
            if (col2Val == null || col2Val.isEmpty()) {
                continue;
            }
            TableRuleConfig tr = tc.getRule();
            if (tr != null) {
                for (RuleConfig rc : tr.getRules()) {
                    boolean match = true;
                    for (String ruleColumn : rc.getColumns()) {
                        match &= col2Val.containsKey(ruleColumn);
                    }
                    if (match) {
                        columnValues = col2Val;
                        rule = rc;
                        matchedTable = tc;
                        outerBreak = true;
                        break;
                    }
                }
            }
            if (outerBreak) {
                break;
            }
        }

        //规则匹配处理，表级别和列级别。
        if (matchedTable == null) {
        }
        return null;
    }

}
