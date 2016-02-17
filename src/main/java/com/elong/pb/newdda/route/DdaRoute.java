package com.elong.pb.newdda.route;

import com.elong.pb.newdda.config.SchemaConfig;
import com.elong.pb.newdda.config.TableConfig;
import com.elong.pb.newdda.config.rule.RuleConfig;
import com.elong.pb.newdda.config.rule.TableRuleConfig;
import com.elong.pb.newdda.parser.ast.ASTNode;
import com.elong.pb.newdda.parser.ast.expression.Expression;
import com.elong.pb.newdda.parser.ast.expression.primary.Identifier;
import com.elong.pb.newdda.parser.ast.stmt.SQLStatement;
import com.elong.pb.newdda.parser.ast.stmt.dal.DALShowStatement;
import com.elong.pb.newdda.parser.ast.stmt.dml.DMLSelectStatement;
import com.elong.pb.newdda.parser.ast.stmt.dml.DMLSelectUnionStatement;
import com.elong.pb.newdda.parser.ast.stmt.dml.DMLUpdateStatement;
import com.elong.pb.newdda.parser.recognizer.SQLParserDelegate;
import com.elong.pb.newdda.parser.recognizer.mysql.syntax.MySQLParser;
import com.elong.pb.newdda.parser.util.Pair;
import com.elong.pb.newdda.parser.visitor.MySQLOutputASTVisitor;
import com.elong.pb.newdda.route.visitor.PartitionKeyVisitor;
import com.elong.pb.newdda.server.DdaConfigSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLNonTransientException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

/**
 * dda路由工具类
 * Created by zhangyong on 15/8/25.
 */
public class DdaRoute {

    private static final Logger logger = LoggerFactory.getLogger(DdaRoute.class);

    public static RouteResultSet route(String stmt, String schemaId) throws SQLSyntaxErrorException {
        //举例：stmt = "select *  from user where id = 2 and name = 'zhangyong' " 若以id为分区关键字;

        RouteResultSet rrs = new RouteResultSet(stmt);

        stmt = "select *  from user where id = 2 and name = 'zhangyong' ";

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
        SQLStatement ast = SQLParserDelegate.parse(stmt, MySQLParser.DEFAULT_CHARSET);
        PartitionKeyVisitor visitor = new PartitionKeyVisitor(schemaConfig.getTables());
        visitor.setTrimSchema(schemaConfig.isKeepSqlSchema() ? schemaConfig.getName() : null);
        ast.accept(visitor);

        // 如果sql包含用户自定义的schema，则路由到default节点
        if (schemaConfig.isKeepSqlSchema() && visitor.isCustomedSchema()) {
            if (visitor.isSchemaTrimmed()) {
                stmt = genSQL(ast, stmt);
            }
            RouteResultsetNode[] nodes = new RouteResultsetNode[1];
            nodes[0] = new RouteResultsetNode(schemaConfig.getDataNode(), stmt);
            rrs.setNodes(nodes);
            return rrs;
        }

        // 元数据语句路由
        if (visitor.isTableMetaRead()) {
            MetaRouter.routeForTableMeta(rrs, schemaConfig, ast, visitor, stmt);
            if (visitor.isNeedRewriteField()) {
                rrs.setFlag(RouteResultSet.REWRITE_FIELD);
            }
            return rrs;
        }

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

        //规则匹配处理，表级别和列级别
        if (matchedTable == null) {
            String sql = visitor.isSchemaTrimmed() ? genSQL(ast, stmt) : stmt;
            RouteResultsetNode[] rn = new RouteResultsetNode[1];
            if ("".equals(schemaConfig.getDataNode()) && isSystemReadSQL(ast)) {
                rn[0] = new RouteResultsetNode(schemaConfig.getRandomDataNode(), sql);
            } else {
                rn[0] = new RouteResultsetNode(schemaConfig.getDataNode(), sql);
            }
            rrs.setNodes(rn);
            return rrs;
        }

        if (rule == null) {
            if (matchedTable.isRuleRequired()) {
                throw new IllegalArgumentException("route rule for table " + matchedTable.getName() + " is required: "
                        + stmt);
            }
            String[] dataNodes = matchedTable.getDataNodes();
            String sql = visitor.isSchemaTrimmed() ? genSQL(ast, stmt) : stmt;
            RouteResultsetNode[] rn = new RouteResultsetNode[dataNodes.length];
            for (int i = 0; i < dataNodes.length; ++i) {
                rn[i] = new RouteResultsetNode(dataNodes[i], sql);
            }
            rrs.setNodes(rn);
            setGroupFlagAndLimit(rrs, visitor);
            return rrs;
        }

        return null;
    }


    private static void validateAST(SQLStatement ast, TableConfig tc, RuleConfig rule, PartitionKeyVisitor visitor) throws SQLNonTransientException {
        if (ast instanceof DMLUpdateStatement) {
            List<Identifier> columns = null;
            List<String> ruleCols = rule.getColumns();
            DMLUpdateStatement update = (DMLUpdateStatement) ast;
            for (Pair<Identifier, Expression> pair : update.getValues()) {
                for (String ruleCol : ruleCols) {
                    if (equals(pair.getKey().getIdTextUpUnescape(), ruleCol)) {
                        if (columns == null) {
                            columns = new ArrayList<Identifier>(ruleCols.size());
                        }
                        columns.add(pair.getKey());
                    }
                }
            }
            if (columns == null) {
                return;
            }
            Map<String, String> alias = visitor.getTableAlias();
            for (Identifier column : columns) {
                String table = column.getLevelUnescapeUpName(2);
                table = alias.get(table);
                if (table != null && table.equals(tc.getName())) {
                    throw new SQLFeatureNotSupportedException("partition key cannot be changed");
                }
            }
        }
    }

    private static class MetaRouter {

        public static void routeForTableMeta(RouteResultSet rrs, SchemaConfig schema, SQLStatement ast, PartitionKeyVisitor visitor, String stmt) {
            String sql = stmt;
            if (visitor.isSchemaTrimmed()) {
                sql = genSQL(ast, stmt);
            }
            String[] tables = visitor.getMetaReadTable();
            if (tables == null) {
                throw new IllegalArgumentException("route err: tables[] is null for meta read table: " + stmt);
            }
            String[] dataNodes;
            if (tables.length <= 0) {
                dataNodes = schema.getMetaDataNodes();
            } else if (tables.length == 1) {
                dataNodes = new String[1];
                dataNodes[0] = getMetaReadDataNode(schema, tables[0]);
            } else {
                Set<String> dataNodeSet = new HashSet<String>(tables.length, 1);
                for (String table : tables) {
                    String dataNode = getMetaReadDataNode(schema, table);
                    dataNodeSet.add(dataNode);
                }
                dataNodes = new String[dataNodeSet.size()];
                Iterator<String> iter = dataNodeSet.iterator();
                for (int i = 0; i < dataNodes.length; ++i) {
                    dataNodes[i] = iter.next();
                }
            }

            RouteResultsetNode[] nodes = new RouteResultsetNode[dataNodes.length];
            rrs.setNodes(nodes);
            for (int i = 0; i < dataNodes.length; ++i) {
                nodes[i] = new RouteResultsetNode(dataNodes[i], sql);
            }
        }

        private static String getMetaReadDataNode(SchemaConfig schema, String table) {
            String dataNode = schema.getDataNode();
            Map<String, TableConfig> tables = schema.getTables();
            TableConfig tc;
            if (tables != null && (tc = tables.get(table)) != null) {
                String[] dn = tc.getDataNodes();
                if (dn != null && dn.length > 0) {
                    dataNode = dn[0];
                }
            }
            return dataNode;
        }
    }

    private static boolean isSystemReadSQL(SQLStatement ast) {
        if (ast instanceof DALShowStatement) {
            return true;
        }
        DMLSelectStatement select = null;
        if (ast instanceof DMLSelectStatement) {
            select = (DMLSelectStatement) ast;
        } else if (ast instanceof DMLSelectUnionStatement) {
            DMLSelectUnionStatement union = (DMLSelectUnionStatement) ast;
            if (union.getSelectStmtList().size() == 1) {
                select = union.getSelectStmtList().get(0);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return select.getTables() == null;
    }

    private static Set<Pair<Expression, ASTNode>> getExpressionSet(Map<Object, Set<Pair<Expression, ASTNode>>> map, Object value) {
        if (map == null || map.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Pair<Expression, ASTNode>> set = map.get(value);
        if (set == null) {
            return Collections.emptySet();
        }
        return set;
    }

    private static String genSQL(SQLStatement ast, String orginalSql) {
        StringBuilder s = new StringBuilder();
        ast.accept(new MySQLOutputASTVisitor(s));
        return s.toString();
    }

    private static void setGroupFlagAndLimit(RouteResultSet rrs, PartitionKeyVisitor visitor) {
        rrs.setLimitSize(visitor.getLimitSize());
        switch (visitor.getGroupFuncType()) {
            case PartitionKeyVisitor.GROUP_SUM:
                rrs.setFlag(RouteResultSet.SUM_FLAG);
                break;
            case PartitionKeyVisitor.GROUP_MAX:
                rrs.setFlag(RouteResultSet.MAX_FLAG);
                break;
            case PartitionKeyVisitor.GROUP_MIN:
                rrs.setFlag(RouteResultSet.MIN_FLAG);
                break;
        }
    }

    private static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

}
