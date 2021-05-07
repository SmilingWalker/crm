package com.frank.crm.workerbench.service.impl;

import com.frank.crm.settings.dao.UserDao;
import com.frank.crm.settings.domain.User;
import com.frank.crm.utils.SqlSessionUtil;
import com.frank.crm.vo.PageListVO;
import com.frank.crm.workerbench.dao.ActivityDao;
import com.frank.crm.workerbench.dao.ActivityRemarkDao;
import com.frank.crm.workerbench.domain.Activity;
import com.frank.crm.workerbench.domain.ActivityRemark;
import com.frank.crm.workerbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity activity) {
        // 调用DAO 进行活动的保存
        int result =  activityDao.save(activity);
        if (result!=1){
            return false;
        }
        return true;
    }

    @Override
    public List<Activity> getActivityListByCondition(Map<String, Object> map) {
        //调用DAO操作数据层，进行数据库查询
        List<Activity> pageList = activityDao.QueryActivityListByCondition(map);
        return pageList;
    }

    @Override
    public int getCount(Map<String, Object> map) {
        int count = activityDao.getCount(map);
        return count;
    }

    @Override
    public PageListVO<Activity> PageList(Map<String, Object> map) {
        PageListVO<Activity> pageListVO = new PageListVO<>();

        int count = activityDao.getCount(map);
        pageListVO.setTotal(count);

        List<Activity> activities = activityDao.QueryActivityListByCondition(map);
        pageListVO.setDataList(activities);

        return pageListVO;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        int RemarkCount = activityRemarkDao.getCountById(ids);

        int deleteRes = activityRemarkDao.deleteByIds(ids);

        if (deleteRes!=RemarkCount){
            flag=false;
        }

        int acCount = activityDao.deleteByIds(ids);

        if (acCount!=ids.length){
            flag = false;
        }


        return flag;

    }

    @Override
    public Map<String, Object> getUserListAndAct(String id) {
        Map<String,Object> map = new HashMap<>();

        List<User> userList = userDao.getUserList();
        map.put("ulist",userList);

        Activity activity = activityDao.getActById(id);
        map.put("activity",activity);

        return map;
    }

    @Override
    public boolean update(Activity activity) {
        // 调用DAO 进行活动的修改
        int result =  activityDao.update(activity);
        if (result!=1){
            return false;
        }
        return true;
    }

    @Override
    public Activity detail(String id) {
        Activity activity = activityDao.detail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkByAid(String aid) {
        List<ActivityRemark> remarkList = activityRemarkDao.getRemarkByAid(aid);
        return remarkList;
    }

    @Override
    public boolean deleteRemarkById(String id) {
        boolean flag;

        flag = activityRemarkDao.deleteById(id);

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.save(activityRemark);
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.update(activityRemark);
        if (count!=1){
            flag = false;
        }
        return flag;
    }
}
