package com.frank.crm.settings.service.impl;

import com.frank.crm.settings.dao.DicTypeDao;
import com.frank.crm.settings.dao.DictValueDao;
import com.frank.crm.settings.domain.DicType;
import com.frank.crm.settings.domain.DictValue;
import com.frank.crm.settings.service.DictionaryService;
import com.frank.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryServiceImpl implements DictionaryService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DictValueDao dictValueDao = SqlSessionUtil.getSqlSession().getMapper(DictValueDao.class);

    @Override
    public Map<String, List<DictValue>> getAll() {
        //首先需要获取所有dicType，根据Type创建map对象
        List<String> TypeNames = dicTypeDao.selectAllCode();

        Map<String,List<DictValue>> map = new HashMap<>();
        for (String code:TypeNames){
            List<DictValue> dictValues = dictValueDao.getValueByCode(code);
            map.put(code,dictValues);
        }
        return map;
    }
}
