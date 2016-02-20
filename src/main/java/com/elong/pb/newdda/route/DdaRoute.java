package com.elong.pb.newdda.route;

import com.elong.pb.newdda.common.CollectionUtil;
import com.elong.pb.newdda.config.SchemaConfig;
import com.elong.pb.newdda.config.TableConfig;
import com.elong.pb.newdda.config.rule.RuleAlgorithm;
import com.elong.pb.newdda.config.rule.RuleConfig;
import com.elong.pb.newdda.config.rule.TableRuleConfig;
import com.elong.pb.newdda.parser.ast.ASTNode;
import com.elong.pb.newdda.parser.ast.expression.Expression;
import com.elong.pb.newdda.parser.ast.expression.ReplacableExpression;
import com.elong.pb.newdda.parser.ast.expression.comparison.InExpression;
import com.elong.pb.newdda.parser.ast.expression.misc.InExpressionList;
import com.elong.pb.newdda.parser.ast.expression.primary.Identifier;
import com.elong.pb.newdda.parser.ast.expression.primary.RowExpression;
import com.elong.pb.newdda.parser.ast.stmt.SQLStatement;
import com.elong.pb.newdda.parser.ast.stmt.dal.DALShowStatement;
import com.elong.pb.newdda.parser.ast.stmt.dml.DMLInsertReplaceStatement;
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
import java.util.*;

/**
 * dda路由工具类
 * Created by zhangyong on 15/8/25.
 */
public class DdaRoute {

    private static final Logger logger = LoggerFactory.getLogger(DdaRoute.class);

    public static RouteResultSet route(String stmt, String schemaId) throws SQLNonTransientException {
        //举例:stmt = "select *  from user where id = 2 and name = 'zhangyong' "; 若以id为分区关键字;
        RouteResultSet rrs = new RouteResultSet(stmt);

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
                throw new IllegalArgumentException("route rule for table " + matchedTable.getName() + " is required: " + stmt);
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

        validateAST(ast, matchedTable, rule, visitor);
        Map<Integer, List<Object[]>> dnMap = ruleCalculate(matchedTable, rule, columnValues);

        if (dnMap == null || dnMap.isEmpty()) {
            throw new IllegalArgumentException("No target dataNode for rule " + rule);
        }

        // 判断路由结果是单库还是多库
        if (dnMap.size() == 1) {
            String dataNode = matchedTable.getDataNodes()[dnMap.keySet().iterator().next()];
            String sql = visitor.isSchemaTrimmed() ? genSQL(ast, stmt) : stmt;
            RouteResultsetNode[] rn = new RouteResultsetNode[1];
            rn[0] = new RouteResultsetNode(dataNode, sql);
            rrs.setNodes(rn);
        } else {
            RouteResultsetNode[] rn = new RouteResultsetNode[dnMap.size()];
            if (ast instanceof DMLInsertReplaceStatement) {
                DMLInsertReplaceStatement ir = (DMLInsertReplaceStatement) ast;
                dispatchInsertReplace(rn, ir, rule.getColumns(), dnMap, matchedTable, stmt, visitor);
            } else {
                dispatchWhereBasedStmt(rn, ast, rule.getColumns(), dnMap, matchedTable, stmt, visitor);
            }
            rrs.setNodes(rn);
            setGroupFlagAndLimit(rrs, visitor);
        }
        return rrs;
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

    private static Map<Integer, List<Object[]>> ruleCalculate(TableConfig matchedTable, RuleConfig rule, Map<String, List<Object>> columnValues) {
        Map<Integer, List<Object[]>> map = new HashMap<Integer, List<Object[]>>(1, 1);
        RuleAlgorithm algorithm = rule.getRuleAlgorithm();
        List<String> cols = rule.getColumns();
        Map<String, Object> parameter = new HashMap<String, Object>(cols.size(), 1);
        ArrayList<Iterator<Object>> colsValIter = new ArrayList<Iterator<Object>>(columnValues.size());
        for (String rc : cols) {
            List<Object> list = columnValues.get(rc);
            if (list == null) {
                String msg = "route err: rule column " + rc + " dosn't exist in extract: " + columnValues;
                throw new IllegalArgumentException(msg);
            }
            colsValIter.add(list.iterator());
        }
        try {
            for (Iterator<Object> mainIter = colsValIter.get(0); mainIter.hasNext(); ) {
                Object[] tuple = new Object[cols.size()];
                for (int i = 0, len = cols.size(); i < len; ++i) {
                    Object value = colsValIter.get(i).next();
                    tuple[i] = value;
                    parameter.put(cols.get(i), value);
                }
                Integer[] dataNodeIndexes = calcDataNodeIndexesByFunction(algorithm, parameter);
                for (int i = 0; i < dataNodeIndexes.length; ++i) {
                    Integer dataNodeIndex = dataNodeIndexes[i];
                    List<Object[]> list = map.get(dataNodeIndex);
                    if (list == null) {
                        list = new LinkedList<Object[]>();
                        map.put(dataNodeIndex, list);
                    }
                    list.add(tuple);
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            String msg = "route err: different rule columns should have same value number:  " + columnValues;
            throw new IllegalArgumentException(msg, e);
        }
        return map;
    }

    private static Integer[] calcDataNodeIndexesByFunction(RuleAlgorithm algorithm, Map<String, Object> parameter) {
        Integer[] dataNodeIndexes;
        Object calRst = algorithm.calculate(parameter);
        if (calRst instanceof Number) {
            dataNodeIndexes = new Integer[1];
            dataNodeIndexes[0] = ((Number) calRst).intValue();
        } else if (calRst instanceof Integer[]) {
            dataNodeIndexes = (Integer[]) calRst;
        } else if (calRst instanceof int[]) {
            int[] intArray = (int[]) calRst;
            dataNodeIndexes = new Integer[intArray.length];
            for (int i = 0; i < intArray.length; ++i) {
                dataNodeIndexes[i] = intArray[i];
            }
        } else {
            throw new IllegalArgumentException("route err: result of route function is wrong type or null: " + calRst);
        }
        return dataNodeIndexes;
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

    private static void dispatchInsertReplace(RouteResultsetNode[] rn, DMLInsertReplaceStatement stmt, List<String> ruleColumns, Map<Integer, List<Object[]>> dataNodeMap, TableConfig matchedTable, String originalSQL, PartitionKeyVisitor visitor) {
        if (stmt.getSelect() != null) {
            dispatchWhereBasedStmt(rn, stmt, ruleColumns, dataNodeMap, matchedTable, originalSQL, visitor);
            return;
        }
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> colsIndex = visitor.getColumnIndex(stmt.getTable()
                .getIdTextUpUnescape());
        if (colsIndex == null || colsIndex.isEmpty()) {
            throw new IllegalArgumentException("columns index is empty: " + originalSQL);
        }
        ArrayList<Map<Object, Set<Pair<Expression, ASTNode>>>> colsIndexList = new ArrayList<Map<Object, Set<Pair<Expression, ASTNode>>>>(
                ruleColumns.size());
        for (int i = 0, len = ruleColumns.size(); i < len; ++i) {
            colsIndexList.add(colsIndex.get(ruleColumns.get(i)));
        }
        int dataNodeId = -1;
        for (Map.Entry<Integer, List<Object[]>> en : dataNodeMap.entrySet()) {
            List<Object[]> tuples = en.getValue();
            HashSet<RowExpression> replaceRowList = new HashSet<RowExpression>(tuples.size());
            for (Object[] tuple : tuples) {
                Set<Pair<Expression, ASTNode>> tupleExprs = null;
                for (int i = 0; i < tuple.length; ++i) {
                    Map<Object, Set<Pair<Expression, ASTNode>>> valueMap = colsIndexList.get(i);
                    Object value = tuple[i];
                    Set<Pair<Expression, ASTNode>> set = getExpressionSet(valueMap, value);
                    tupleExprs = (Set<Pair<Expression, ASTNode>>) CollectionUtil.intersectSet(tupleExprs, set);
                }
                if (tupleExprs == null || tupleExprs.isEmpty()) {
                    throw new IllegalArgumentException("route: empty expression list for insertReplace stmt: "
                            + originalSQL);
                }
                for (Pair<Expression, ASTNode> p : tupleExprs) {
                    if (p.getValue() == stmt && p.getKey() instanceof RowExpression) {
                        replaceRowList.add((RowExpression) p.getKey());
                    }
                }
            }

            stmt.setReplaceRowList(new ArrayList<RowExpression>(replaceRowList));
            String sql = genSQL(stmt, originalSQL);
            stmt.clearReplaceRowList();
            String dataNodeName = matchedTable.getDataNodes()[en.getKey()];
            rn[++dataNodeId] = new RouteResultsetNode(dataNodeName, sql);
        }
    }

    private static void dispatchWhereBasedStmt(RouteResultsetNode[] rn, SQLStatement stmtAST, List<String> ruleColumns,
                                               Map<Integer, List<Object[]>> dataNodeMap, TableConfig matchedTable,
                                               String originalSQL, PartitionKeyVisitor visitor) {
        // [perf tag] 11.617 us: sharding multivalue
        if (ruleColumns.size() > 1) {
            String sql;
            if (visitor.isSchemaTrimmed()) {
                sql = genSQL(stmtAST, originalSQL);
            } else {
                sql = originalSQL;
            }
            int i = -1;
            for (Integer dataNodeId : dataNodeMap.keySet()) {
                String dataNode = matchedTable.getDataNodes()[dataNodeId];
                rn[++i] = new RouteResultsetNode(dataNode, sql);
            }
            return;
        }

        final String table = matchedTable.getName();
        Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> columnIndex = visitor.getColumnIndex(table);
        Map<Object, Set<Pair<Expression, ASTNode>>> valueMap = columnIndex.get(ruleColumns.get(0));
        replacePartitionKeyOperand(columnIndex, ruleColumns);

        Map<InExpression, Set<Expression>> unreplacedInExpr = new HashMap<InExpression, Set<Expression>>(1, 1);
        Set<ReplacableExpression> unreplacedSingleExprs = new HashSet<ReplacableExpression>();
        // [perf tag] 12.2755 us: sharding multivalue

        int nodeId = -1;
        for (Map.Entry<Integer, List<Object[]>> en : dataNodeMap.entrySet()) {
            List<Object[]> tuples = en.getValue();

            unreplacedSingleExprs.clear();
            unreplacedInExpr.clear();
            for (Object[] tuple : tuples) {
                Object value = tuple[0];
                Set<Pair<Expression, ASTNode>> indexedExpressionPair = getExpressionSet(valueMap, value);
                for (Pair<Expression, ASTNode> pair : indexedExpressionPair) {
                    Expression expr = pair.getKey();
                    ASTNode parent = pair.getValue();
                    if (PartitionKeyVisitor.isPartitionKeyOperandSingle(expr, parent)) {
                        unreplacedSingleExprs.add((ReplacableExpression) expr);
                    } else if (PartitionKeyVisitor.isPartitionKeyOperandIn(expr, parent)) {
                        Set<Expression> newInSet = unreplacedInExpr.get(parent);
                        if (newInSet == null) {
                            newInSet = new HashSet<Expression>(indexedExpressionPair.size(), 1);
                            unreplacedInExpr.put((InExpression) parent, newInSet);
                        }
                        newInSet.add(expr);
                    }
                }
            }
            // [perf tag] 15.3745 us: sharding multivalue

            for (ReplacableExpression expr : unreplacedSingleExprs) {
                expr.clearReplaceExpr();
            }
            for (Map.Entry<InExpression, Set<Expression>> entemp : unreplacedInExpr.entrySet()) {
                InExpression in = entemp.getKey();
                Set<Expression> set = entemp.getValue();
                if (set == null || set.isEmpty()) {
                    in.setReplaceExpr(ReplacableExpression.BOOL_FALSE);
                } else {
                    in.clearReplaceExpr();
                    InExpressionList inlist = in.getInExpressionList();
                    if (inlist != null)
                        inlist.setReplaceExpr(new ArrayList<Expression>(set));
                }
            }
            // [perf tag] 16.506 us: sharding multivalue

            String sql = genSQL(stmtAST, originalSQL);
            // [perf tag] 21.3425 us: sharding multivalue

            String dataNodeName = matchedTable.getDataNodes()[en.getKey()];
            rn[++nodeId] = new RouteResultsetNode(dataNodeName, sql);

            for (ReplacableExpression expr : unreplacedSingleExprs) {
                expr.setReplaceExpr(ReplacableExpression.BOOL_FALSE);
            }
            for (InExpression in : unreplacedInExpr.keySet()) {
                in.setReplaceExpr(ReplacableExpression.BOOL_FALSE);
                InExpressionList list = in.getInExpressionList();
                if (list != null)
                    list.clearReplaceExpr();
            }
            // [perf tag] 22.0965 us: sharding multivalue
        }
    }

    private static void replacePartitionKeyOperand(Map<String, Map<Object, Set<Pair<Expression, ASTNode>>>> index, List<String> cols) {
        if (cols == null) {
            return;
        }
        for (String col : cols) {
            Map<Object, Set<Pair<Expression, ASTNode>>> map = index.get(col);
            if (map == null) {
                continue;
            }
            for (Set<Pair<Expression, ASTNode>> set : map.values()) {
                if (set == null) {
                    continue;
                }
                for (Pair<Expression, ASTNode> p : set) {
                    Expression expr = p.getKey();
                    ASTNode parent = p.getValue();
                    if (PartitionKeyVisitor.isPartitionKeyOperandSingle(expr, parent)) {
                        ((ReplacableExpression) expr).setReplaceExpr(ReplacableExpression.BOOL_FALSE);
                    } else if (PartitionKeyVisitor.isPartitionKeyOperandIn(expr, parent)) {
                        ((ReplacableExpression) parent).setReplaceExpr(ReplacableExpression.BOOL_FALSE);
                    }
                }
            }
        }
    }

}
