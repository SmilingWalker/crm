package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountById(String[] ids);

    int deleteByIds(String[] ids);

    List<ActivityRemark> getRemarkByAid(String aid);

    boolean deleteById(String id);

    int save(ActivityRemark activityRemark);

    int update(ActivityRemark activityRemark);
}
