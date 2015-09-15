package com.elong.pb.newdda.config.loader;

import com.elong.pb.newdda.common.SplitUtil;
import com.elong.pb.newdda.config.ConfigUtil;
import com.elong.pb.newdda.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

/**
 * Schema 相关的配置加载
 * Created by zhangyong on 15/9/3.
 */
public class SchemaConfigLoader {

    private final static Logger logger = LoggerFactory.getLogger(SchemaConfigLoader.class);

    public void loadSchemaConfig() {
        InputStream xml = null;
        try {
            xml = RuleConfigLoader.class.getResourceAsStream("/schema.xml");
            Document document = ConfigUtil.getDocument(xml);
            Element root = document.getDocumentElement();
            loadDataNode(root);
            loadSchemaNode(root);
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

    //加载数据节点
    private void loadDataNode(Element root) {
        NodeList list = root.getElementsByTagName("dataNode");
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Element element = (Element) list.item(i);
            String dnNamePrefix = element.getAttribute("name");
            try {
                Element dsElement = findPropertyByName(element, "dataSource");
                if (dsElement == null) {
                    throw new NullPointerException("dataNode xml Element with name of " + dnNamePrefix + " has no dataSource Element");
                }
                NodeList dataSourceList = dsElement.getElementsByTagName("dataSourceRef");
                String dataSources[][] = new String[dataSourceList.getLength()][];
                for (int j = 0, m = dataSourceList.getLength(); j < m; ++j) {
                    Element ref = (Element) dataSourceList.item(j);
                    String dsString = ref.getTextContent();
                    dataSources[j] = SplitUtil.split(dsString, ',', '$', '-', '[', ']');
                }
                if (dataSources.length <= 0) {
                    throw new ConfigException("no dataSourceRef defined!");
                }
                for (String[] dss : dataSources) {
                    if (dss.length != dataSources[0].length) {
                        throw new ConfigException("dataSource number not equals!");
                    }
                }
            } catch (Exception e) {
                throw new ConfigException("dataNode:" + dnNamePrefix);
            }
        }
    }

    private void loadSchemaNode(Element root) {

    }

    private static Element findPropertyByName(Element bean, String name) {
        NodeList propertyList = bean.getElementsByTagName("property");
        for (int j = 0, m = propertyList.getLength(); j < m; ++j) {
            Node node = propertyList.item(j);
            if (node instanceof Element) {
                Element p = (Element) node;
                if (name.equals(p.getAttribute("name"))) {
                    return p;
                }
            }
        }
        return null;
    }

}
