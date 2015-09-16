package com.elong.pb.newdda.config;

/**
 * schema的dataNode节点配置
 * Created by zhangyong on 15/9/15.
 */
public class DataNodeConfig {

    private String name;

    private String dataSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String toString(){
        return new StringBuffer() .
                append("name:").
                append(name).
                append(" dataSource:").
                append(dataSource).toString() ;
    }

}
