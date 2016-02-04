package com.elong.pb.newdda.druid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 测试jdbc
 * Created by zhangyong on 16/2/2.
 */
public class JdbcTest {

    @Before
    public void setUp() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJdbcConnect() {
        String url = "jdbc:mysql://localhost:8888/pbAccount";
        String username = "root";
        String password = "ilxw";
        try {
            Connection con = DriverManager.getConnection(url, username, password);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @After
    public void destory() {

    }

}
