package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by zhangyong on 14/11/22.
 * jdbc测试
 */
public class JdbcTest {

    @Test
    public void getBlog() throws Exception {
        String url = "jdbc:mysql://localhost:8066/pb_account?user=test&password=test";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                for (int i = 0; i < 1; i++) {
                    long start = System.currentTimeMillis();
                    statement = connection.prepareStatement("select * from user where name= ?");
                    statement.setString(1, "177");
                    rs = statement.executeQuery();
                    while (rs.next()) {
                        System.out.println(rs.getString("name"));
                    }
                    System.out.println("cost:" + (System.currentTimeMillis() - start));
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
    }

    @Test
    public void insert() throws Exception {
        String url = "jdbc:mysql://localhost:8066/pb_account?user=test&password=test";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.prepareStatement("insert into user(name) values(?)");
                statement.setString(1, "lilin");
                int result = statement.executeUpdate();
                System.out.println(result);
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
    }

}
