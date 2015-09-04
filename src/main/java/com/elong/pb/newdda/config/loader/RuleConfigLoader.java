package com.elong.pb.newdda.config.loader;

import com.elong.pb.newdda.common.SplitUtil;
import com.elong.pb.newdda.config.ConfigUtil;
import com.elong.pb.newdda.config.ParameterMapping;
import com.elong.pb.newdda.config.rule.RuleAlgorithm;
import com.elong.pb.newdda.config.rule.RuleConfig;
import com.elong.pb.newdda.config.rule.TableRuleConfig;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 规则加载器
 * Created by zhangyong on 15/9/3.
 */
public class RuleConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(RuleConfigLoader.class);

    public void loadRuleConfig() {
        InputStream xml = null;
        try {
            xml = RuleConfigLoader.class.getResourceAsStream("/rule.xml");
            Document document = ConfigUtil.getDocument(xml);
            Element root = document.getDocumentElement();
            loadFunctions(root);
            loadTableRules(root);
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

    private void loadFunctions(Element root) throws ConfigException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, RuleAlgorithm> functions = DdaConfigSingleton.getInstance().getFunctions();
        NodeList list = root.getElementsByTagName("function");
        for (int i = 0, n = list.getLength(); i < n; ++i) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element e = (Element) node;
                String name = e.getAttribute("name");
                if (functions.containsKey(name)) {
                    throw new ConfigException("rule function " + name + " duplicated!");
                }
                String clazz = e.getAttribute("class");
                //kfaka 关注日志相关 metaq长轮询的方式
                RuleAlgorithm function = createFunction(name, clazz);
                //给function赋予值
                ParameterMapping.mapping(function, ConfigUtil.loadElements(e));
                functions.put(name, function);
            }
        }
    }

    private RuleAlgorithm createFunction(String name, String clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clz = Class.forName(clazz);
        if (!RuleAlgorithm.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException("rule function must implements " + RuleAlgorithm.class.getName()
                    + ", name=" + name);
        }
        Constructor<?> constructor = null;
        for (Constructor<?> cons : clz.getConstructors()) {
            Class<?>[] paraClzs = cons.getParameterTypes();
            if (paraClzs != null && paraClzs.length == 1) {
                Class<?> paraClzs1 = paraClzs[0];
                if (String.class.isAssignableFrom(paraClzs1)) {
                    constructor = cons;
                    break;
                }
            }
        }
        if (constructor == null) {
            throw new ConfigException("function " + name + " with class of " + clazz
                    + " must have a constructor with one parameter: String funcName");
        }
        return (RuleAlgorithm) constructor.newInstance(name);
    }

    private void loadTableRules(Element root) throws SQLSyntaxErrorException {
        Map<String, TableRuleConfig> tableRules = DdaConfigSingleton.getInstance().getTableRules();
        Set<RuleConfig> rules = DdaConfigSingleton.getInstance().getRules();
        NodeList list = root.getElementsByTagName("tableRule");
        for (int i = 0, n = list.getLength(); i < n; ++i) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element e = (Element) node;
                String name = e.getAttribute("name");
                if (tableRules.containsKey(name)) {
                    throw new ConfigException("table rule " + name + " duplicated!");
                }
                NodeList ruleNodes = e.getElementsByTagName("rule");
                int length = ruleNodes.getLength();
                List<RuleConfig> ruleList = new ArrayList<RuleConfig>(length);
                for (int j = 0; j < length; ++j) {
                    RuleConfig rule = loadRule((Element) ruleNodes.item(j));
                    ruleList.add(rule);
                    rules.add(rule);
                }
                tableRules.put(name, new TableRuleConfig(name, ruleList));
            }
        }
    }

    private RuleConfig loadRule(Element element) throws SQLSyntaxErrorException {
        Element columnsEle = ConfigUtil.loadElement(element, "columns");
        String[] columns = SplitUtil.split(columnsEle.getTextContent(), ',', true);
        for (int i = 0; i < columns.length; ++i) {
            columns[i] = columns[i].toUpperCase();
        }
        Element algorithmEle = ConfigUtil.loadElement(element, "algorithm");
        String algorithm = algorithmEle.getTextContent();
        return new RuleConfig(columns, algorithm);
    }

}
