package com.frank.crm.workerbench.service;

import com.frank.crm.vo.PageListVO;
import com.frank.crm.workerbench.domain.Activity;
import com.frank.crm.workerbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int getCount(Map<String, Object> map);

    PageListVO<Activity> PageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndAct(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkByAid(String aid);

    boolean deleteRemarkById(String id);

    boolean saveRemark(ActivityRemark activityRemark);

    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActByClueId(String id);

    List<Activity> getCandidateActList(Map<String, String> map);

    List<Activity> getCandidateActById(Map map);
}
