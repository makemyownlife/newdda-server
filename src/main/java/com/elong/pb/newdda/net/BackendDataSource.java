package com.elong.pb.newdda.net;

import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.route.DdaRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyong on 15/2/11.
 * 后端数据源虚拟 包含多个数据源
 */
public class BackendDataSource {

    private List<BackendChannelPool> backendChannelPoolList;

    private DataSourceConfig dataSourceConfig;

    private String id;

    public BackendDataSource(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.id = dataSourceConfig.getId();
    }

    public String getId() {
        return id;
    }

    public void init() {
        List<DataSourceLocation> locationList = this.dataSourceConfig.getDataSourceLocationList();
        this.backendChannelPoolList = new ArrayList<BackendChannelPool>(locationList.size());
        for (DataSourceLocation dataSourceLocation : locationList) {
            BackendChannelPool backendChannelPool = new BackendChannelPool(
                    dataSourceConfig,
                    dataSourceLocation,
                    dataSourceConfig.getMaxconn());
            backendChannelPool.initMinConn();
            //添加DataSourceLocation 与 后端连接池的对应关系
            DdaRoute.addLocationPoolMapping(dataSourceLocation, backendChannelPool);
        }
    }

}
