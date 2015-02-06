/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elong.pb.newdda.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述一个数据源的配置
 */
public class DataSourceConfig {

    public DataSourceConfig() {
        this.dataSourceLocationList = new ArrayList<DataSourceLocation>();
    }

    private List<DataSourceLocation> dataSourceLocationList;

    private Integer initconn;

    private Integer maxconn;

    private String id;

    private String keyname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public Integer getInitconn() {
        return initconn;
    }

    public void setInitconn(Integer initconn) {
        this.initconn = initconn;
    }

    public Integer getMaxconn() {
        return maxconn;
    }

    public void setMaxconn(Integer maxconn) {
        this.maxconn = maxconn;
    }

    public List<DataSourceLocation> getDataSourceLocationList() {
        return dataSourceLocationList;
    }

    public void setDataSourceLocationList(List<DataSourceLocation> dataSourceLocationList) {
        this.dataSourceLocationList = dataSourceLocationList;
    }


}
