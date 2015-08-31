package com.elong.pb.newdda.server;

import com.elong.pb.newdda.config.ConfigUtil;
import com.elong.pb.newdda.config.DataSourceConfig;
import com.elong.pb.newdda.config.DataSourceLocation;
import com.elong.pb.newdda.exception.ConfigException;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 加载中间件的数据库配置
 * Created by zhangyong on 15/8/3.
 */
public class DdaConfigLoader {

    public static final Logger logger = LoggerFactory.getLogger(DdaConfigLoader.class);

    private final AtomicBoolean inited = new AtomicBoolean(false);

    private static DdaConfigLoader INSTANCE = new DdaConfigLoader();

    public static DdaConfigLoader getInstance() {
        return INSTANCE;
    }

    public void init() {
        if (this.inited.compareAndSet(false, true)) {
            //加载数据源配置
            loadDataSourceConfig();
            //加载分库规则配置
            loadRuleConfig();
            //加载模块配置
            loadSchemaConfig();
        }
    }

    private void loadDataSourceConfig() {
        InputStream xml = null;
        try {
            xml = DdaConfigLoader.class.getResourceAsStream("/datasource.xml");
            Document document = ConfigUtil.getDocument(xml);
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("datasource");
            for (int i = 0; i < list.getLength(); i++) {
                Element dataSource = (Element) list.item(i);
                String id = dataSource.getAttribute("id");
                DataSourceConfig dataSourceConfig = new DataSourceConfig();
                dataSourceConfig.setId(id);
                NodeList propertiesList = dataSource.getElementsByTagName("property");
                for (int j = 0; j < propertiesList.getLength(); j++) {
                    Element pro = (Element) propertiesList.item(j);
                    String proName = pro.getAttribute("name");
                    if (!"location".equals(proName)) {
                        BeanUtils.copyProperty(dataSourceConfig, proName, pro.getTextContent());
                    } else {
                        List<DataSourceLocation> dataSourceLocationList = new ArrayList<DataSourceLocation>();
                        NodeList locationNodes = pro.getElementsByTagName("location");
                        for (int k = 0; k < locationNodes.getLength(); k++) {
                            Element locationNode = (Element) locationNodes.item(k);
                            String location = locationNode.getTextContent();
                            String[] arr = location.split(":");
                            DataSourceLocation dataSourceLocation = new DataSourceLocation();
                            // 127.0.0.1:3306:pb_account:test:nopass.2
                            dataSourceLocation.setHost(arr[0]);
                            dataSourceLocation.setPort(Integer.valueOf(arr[1]));
                            dataSourceLocation.setDatabaseName(arr[2]);
                            dataSourceLocation.setUser(arr[3]);
                            dataSourceLocation.setPassword(arr[4]);

                            dataSourceLocationList.add(dataSourceLocation);
                        }
                        dataSourceConfig.setDataSourceLocationList(dataSourceLocationList);
                    }
                }
                DdaConfigSingleton.getInstance().getDataSources().put(id, dataSourceConfig);
            }
        } catch (ConfigException e) {
            throw e;
        } catch (Throwable e) {
            throw new ConfigException(e);
        } finally {
            if (xml != null) {
                try {
                    xml.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void loadRuleConfig() {
        InputStream xml = null;
        try {
            xml = DdaConfigLoader.class.getResourceAsStream("/rule.xml");
            Document document = ConfigUtil.getDocument(xml);
            Element root = document.getDocumentElement();
            //先加载 function
            NodeList functionNodeList = root.getElementsByTagName("function");
            //后加载 tableRule
            NodeList list = root.getElementsByTagName("tableRule");
        } catch (ConfigException e) {
            throw e;
        } catch (Throwable e) {
            throw new ConfigException(e);
        } finally {
            if (xml != null) {
                try {
                    xml.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void loadSchemaConfig() {

    }

}
