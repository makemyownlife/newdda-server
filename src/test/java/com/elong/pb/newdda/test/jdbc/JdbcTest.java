package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by zhangyong on 14/11/22.
 * jdbc测试
 */
public class JdbcTest {

    @Test
    public void getBlog() throws Exception {
        System.out.println(123);
        String url = "jdbc:mysql://localhost:8066/pb_account?user=test&password=test";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);

        if (connection != null) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from posts where id = 1");
            while (rs.next()) {
                System.out.println(rs.getString("text"));

            }
        }
    }

}
