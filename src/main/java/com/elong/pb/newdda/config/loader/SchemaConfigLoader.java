package com.elong.pb.newdda.config.loader;

import com.elong.pb.newdda.common.SplitUtil;
import com.elong.pb.newdda.config.ConfigUtil;
import com.elong.pb.newdda.config.DataNodeConfig;
import com.elong.pb.newdda.config.ParameterMapping;
import com.elong.pb.newdda.exception.ConfigException;
import com.elong.pb.newdda.server.DdaConfigSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<DataNodeConfig> confList = new ArrayList<DataNodeConfig>();
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
                for (int k = 0, limit = dataSources[0].length; k < limit; ++k) {
                    StringBuilder dsString = new StringBuilder();
                    for (int dsIndex = 0; dsIndex < dataSources.length; ++dsIndex) {
                        if (dsIndex > 0) {
                            dsString.append(',');
                        }
                        dsString.append(dataSources[dsIndex][k]);
                    }
                    DataNodeConfig conf = new DataNodeConfig();
                    ParameterMapping.mapping(conf, ConfigUtil.loadElements(element));
                    confList.add(conf);
                    switch (k) {
                        case 0:
                            conf.setName((limit == 1) ? dnNamePrefix : dnNamePrefix + "[" + k + "]");
                            break;
                        default:
                            conf.setName(dnNamePrefix + "[" + k + "]");
                            break;
                    }
                    conf.setDataSource(dsString.toString());
                }
            } catch (Exception e) {
                throw new ConfigException("dataNode:" + dnNamePrefix);
            }
            Map<String, DataNodeConfig> dataNodes = DdaConfigSingleton.getInstance().getDataNodes();
            for (DataNodeConfig conf : confList) {
                if (dataNodes.containsKey(conf.getName())) {
                    throw new ConfigException("dataNode " + conf.getName() + " duplicated!");
                }
                dataNodes.put(conf.getName(), conf);
            }
            logger.info("dataNodes:{}", dataNodes);
        }
    }

    private void loadSchemaNode(Element root) {
        NodeList list = root.getElementsByTagName("schema");
        for (int i = 0, n = list.getLength(); i < n; i++) {
            Element schemaElement = (Element) list.item(i);
            String name = schemaElement.getAttribute("name");
            String dataNode = schemaElement.getAttribute("dataNode");

        }
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
