package com.elong.pb.newdda.route;

import com.elong.pb.newdda.common.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路由结果集
 * Created by zhangyong on 15/8/25.
 */
public class RouteResultSet {

    private final static Logger logger = LoggerFactory.getLogger(RouteResultSet.class);
    public static final int SUM_FLAG = 1;
    public static final int MIN_FLAG = 2;
    public static final int MAX_FLAG = 3;
    public static final int REWRITE_FIELD = 4;

    private final String statement; // 原始语句
    private RouteResultsetNode[] nodes; // 路由结果节点
    private int flag; // 结果集的处理标识，比如：合并，相加等。
    private long limitSize;

    public RouteResultSet(String stmt) {
        this.statement = stmt;
        this.limitSize = -1;
    }

    public String getStatement() {
        return statement;
    }

    public RouteResultsetNode[] getNodes() {
        return nodes;
    }

    public void setNodes(RouteResultsetNode[] nodes) {
        this.nodes = nodes;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * @return -1 if no limit
     */
    public long getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(long limitSize) {
        this.limitSize = limitSize;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(statement).append(", route={");
        if (nodes != null) {
            for (int i = 0; i < nodes.length; ++i) {
                s.append("\n ").append(FormatUtil.format(i + 1, 3));
                s.append(" -> ").append(nodes[i]);
            }
        }
        s.append("\n}");
        return s.toString();
    }


}
