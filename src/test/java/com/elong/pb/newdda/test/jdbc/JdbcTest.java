package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by zhangyong on 14/11/22.
 * jdbc测试
 */
public class JdbcTest {

    @Test
    public void getBlog() throws Exception {
        System.out.println(123);
        String url = "jdbc:mysql://localhost:8888/blog?user=root&password=ilxw";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
        if (connection != null) {
            System.out.println(connection);
        }
    }

}
