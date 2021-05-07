package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int save(Activity activity);

    List<Activity> QueryActivityListByCondition(Map<String, Object> map);

    int getCount(Map<String,Object> map);

    int deleteByIds(String[] ids);

    Activity getActById(String id);

    int update(Activity activity);

    Activity detail(String id);
}
