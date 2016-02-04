package com.elong.pb.newdda.druid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

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
    public void testJdbcConnect() throws SQLException {
        String url = "jdbc:mysql://localhost:8066/pbAccount";
        String username = "root";
        String password = "ilxw";
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = null;
            rs = null;
            statement = connection.prepareStatement("select * from user where id = 2");
            rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    @After
    public void destory() {

    }

}
