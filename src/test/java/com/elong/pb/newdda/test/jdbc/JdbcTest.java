package com.elong.pb.newdda.test.jdbc;

import org.junit.Test;

import java.sql.DriverManager;

/**
 * Created by zhangyong on 14/11/22.
 * jdbc测试
 */
public class JdbcTest {

    @Test
    public void getBlog() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

    }

}
