package com.elong.pb.newdda.config;

import java.util.UUID;

/**
 * Created by zhangyong on 15/2/6.
 * 数据所在位置
 */
public class DataSourceLocation {

    public DataSourceLocation() {
        this.locationId = UUID.randomUUID().toString();
    }

    private String host;

    private String databaseName;

    private Integer port;

    private String user;

    private String password;

    private String locationId;

    private String address;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return this.host + ":" + this.port;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
