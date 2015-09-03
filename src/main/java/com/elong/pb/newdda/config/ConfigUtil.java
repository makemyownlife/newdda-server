package com.elong.pb.newdda.config;

import com.elong.pb.newdda.common.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyong on 15/2/4.
 * 配置管理
 */
public class ConfigUtil {

    public static Document getDocument(final InputStream dtd, InputStream xml) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) {
                return new InputSource(dtd);
            }
        });
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) {
            }

            @Override
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                throw e;
            }
        });
        return builder.parse(xml);
    }

    public static Document getDocument(
            InputStream xml) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xml);
    }

//    public static Map<String, Object> loadElements(Element parent) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        NodeList children = parent.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//            Node node = children.item(i);
//            if (node instanceof Element) {
//                Element e = (Element) node;
//                String name = e.getNodeName();
//                if ("property".equals(name)) {
//                    String key = e.getAttribute("name");
//                    NodeList nl = e.getElementsByTagName("bean");
//                    if (nl.getLength() == 0) {
//                        String value = e.getTextContent();
//                        map.put(key, StringUtil.isEmpty(value) ? null : value.trim());
//                    } else {
//                        map.put(key, loadBean((Element) nl.item(0)));
//                    }
//                }
//            }
//        }
//        return map;
//    }


}
