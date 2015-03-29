package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

import java.sql.*;

/**
 * Created by zhangyong on 14/11/22.
 * jdbc测试
 */
public class JdbcTest {

    @Test
    public void getBlog() throws Exception {
        System.out.println(123);
    //    String url = "jdbc:mysql://localhost:8066/pb_account?user=test&password=test";
        String url = "jdbc:mysql://localhost:8066/pb_account?user=test&password=test";
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
  //    connection.setAutoCommit(false);
        if (connection != null) {
            for (int i = 0; i < 1; i++) {
                PreparedStatement statement = connection.prepareStatement("select * from user where id= ?");
                statement.setString(1,"1");
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getString("user_name"));
                }
                connection.commit();
                Thread.sleep(5000);
            }
        }
    }

}
