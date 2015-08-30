package com.elong.pb.newdda.parser;

import com.elong.pb.newdda.parser.ast.stmt.SQLStatement;
import com.elong.pb.newdda.parser.ast.stmt.dml.DMLSelectStatement;
import com.elong.pb.newdda.parser.recognizer.SQLParserDelegate;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLSyntaxErrorException;

/**
 * Created by zhangyong on 15/8/30.
 */
public class SQLParserDelegateTest {

    @Before
    public void before(){
        System.out.println("before....");
    }

    @Test
    public void testProperlyEnd() throws SQLSyntaxErrorException {
        String sql = "select * from tb1;";
        SQLStatement stmt = SQLParserDelegate.parse(sql);
        Assert.assertEquals(DMLSelectStatement.class, stmt.getClass());

        sql = "select * from tb1 ;;;  ";
        stmt = SQLParserDelegate.parse(sql);
        Assert.assertEquals(DMLSelectStatement.class, stmt.getClass());

        sql = "select * from tb1 /***/  ";
        stmt = SQLParserDelegate.parse(sql);
        Assert.assertEquals(DMLSelectStatement.class, stmt.getClass());

        sql = "select * from tb1 ,  ";
        try {
            stmt = SQLParserDelegate.parse(sql);
            Assert.fail("should detect inproperly end");
        } catch (SQLSyntaxErrorException e) {
        }

        sql = "select * from tb1 ;,  ";
        try {
            stmt = SQLParserDelegate.parse(sql);
            Assert.fail("should detect inproperly end");
        } catch (SQLSyntaxErrorException e) {
        }
    }

    @After
    public void after(){
        System.out.println("after....");
    }

}
