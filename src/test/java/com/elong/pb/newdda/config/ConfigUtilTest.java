package com.elong.pb.newdda.config;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangyong on 15/9/3.
 */
public class ConfigUtilTest {

    @Test
    public void imitateSendShortMessage() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String template = "${0}为您预订的${1}飞机票，会到时候送到你的手中!";
        List<String> objects = new ArrayList<String>();
        objects.add("张勇");
        objects.add("飞机到杭州");
        for (int i = 0; i < objects.size(); i++) {
            String regexp = "\\$\\{" + i + "\\}";
            template = template.replaceAll(regexp, objects.get(i));
        }
        //相当于从 0 开始 是cdms使用的

        //使用map来表示
        Map<String, String> param = new HashMap<String, String>();
        param.put("username", "zhangyong");
        param.put("password", "test");
        String template2 = "${username}为您预订的${password}飞机票，会到时候送到你的手中!";

        //http://blog.csdn.net/kofandlizi/article/details/7323863
        String reg = "\\$\\{[a-zA-Z]+\\}";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(template2);
        while (matcher.find()) {
            String group = matcher.group();
            String key = group.replaceAll("\\$", "").replaceAll("\\{", "").replaceAll("\\}", "");
            template2 = template2.replaceAll("\\$\\{" + key + "\\}" , BeanUtils.getProperty(param,key));
            System.out.println(template2);
        }

    }

}
