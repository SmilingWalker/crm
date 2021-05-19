package com.frank.crm.web.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frank.crm.settings.domain.DictValue;
import com.frank.crm.settings.service.DictionaryService;
import com.frank.crm.settings.service.impl.DictionaryServiceImpl;
import com.frank.crm.utils.ServiceFactory;
import jdk.nashorn.internal.ir.WhileNode;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.*;

public class DictionaryListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Context域对象已创建");
        ServletContext application =  event.getServletContext();
        //需要获取数据字典，并且进行数据字典保存到当前的域对象内 向相关的service对象进行数据获取请求
        DictionaryService ds = (DictionaryService) ServiceFactory.getService(new DictionaryServiceImpl());

        Map<String,List<DictValue>> map = ds.getAll();

        for (String key:map.keySet()){
            application.setAttribute(key,map.get(key));
        }

        // possibility

        ResourceBundle bundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keys = bundle.getKeys();

        Map<String,String> pMap = new HashMap<>();
        while (keys.hasMoreElements()){
            String stage = keys.nextElement();
            pMap.put(stage,bundle.getString(stage));
        }
        ObjectMapper om = new ObjectMapper();
        String pMpaStr = "";
        try {
            pMpaStr = om.writeValueAsString(pMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        application.setAttribute("pMap",pMap);
        application.setAttribute("pMapStr", pMpaStr);
    }
}
