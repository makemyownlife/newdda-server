package com.elong.pb.newdda.config.loader;

import com.elong.pb.newdda.config.ConfigUtil;
import com.elong.pb.newdda.exception.ConfigException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangyong on 15/2/2.
 * 加载数据源以及路由相关的配置文件
 */
public class XmlConfigLoader {

    public XmlConfigLoader() {
        this.load();
    }

    private void load() {
        InputStream xml = null;
        try {
            xml = XmlConfigLoader.class.getResourceAsStream("/datasource.xml");
            Document document = ConfigUtil.getDocument(xml);
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("dataSource");
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

}
