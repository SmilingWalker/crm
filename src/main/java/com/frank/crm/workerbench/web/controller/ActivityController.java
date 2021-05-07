package com.frank.crm.workerbench.web.controller;

import com.frank.crm.settings.domain.User;
import com.frank.crm.settings.service.UserService;
import com.frank.crm.settings.service.impl.UserServiceImpl;
import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.PrintJson;
import com.frank.crm.utils.ServiceFactory;
import com.frank.crm.utils.UUIDUtil;
import com.frank.crm.vo.PageListVO;
import com.frank.crm.workerbench.domain.Activity;
import com.frank.crm.workerbench.domain.ActivityRemark;
import com.frank.crm.workerbench.service.ActivityService;
import com.frank.crm.workerbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入市场活动控制器");
        String path = request.getServletPath();

        if ("/workbench/activity/saveActivity.do".equals(path)){
            saveActivity(request,response);
        }else if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }
        else if ("/workbench/activity/getActivityByCondition.do".equals(path)){
            getActivityByCondition(request,response);
        }else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if ("/workbench/activity/getUserListAndAct.do".equals(path)){
            getUserListAndAct(request,response);
        }
        else if ("/workbench/activity/updateAct.do".equals(path)){
            updateAct(request,response);
        }else if ("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        } else if ("/workbench/activity/deleteRemarkById.do".equals(path)){
            deleteRemarkById(request,response);
        }else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if ("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }

    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        String editFlag = "1";
        String id = request.getParameter("id");

        ActivityRemark activityRemark = new ActivityRemark();

        activityRemark.setNoteContent(noteContent);
        activityRemark.setId(id);
        activityRemark.setEditFlag(editFlag);
        activityRemark.setEditBy(editBy);
        activityRemark.setEditTime(editTime);


        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("activityRemark",activityRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入添加Remark模块");
        String aid = request.getParameter("aid");
        String noteContent = request.getParameter("noteContent");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String editFlag = "0";
        String id = UUIDUtil.getUUID();

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setActivityId(aid);
        activityRemark.setCreateBy(createBy);
        activityRemark.setCreateTime(createTime);
        activityRemark.setEditFlag(editFlag);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setId(id);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("activityRemark",activityRemark);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemarkById(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemarkById(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("刷新remark列表");
        String aid = request.getParameter("aid");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> remarkList = as.getRemarkByAid(aid);
        PrintJson.printJsonObj(response,remarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("详情页跳转");
        //走后台，得到Activity对象，保存进入Request域内，进行跳转
        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = as.detail(id);

        request.setAttribute("a",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void updateAct(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();

        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        //调用service层进行事务的处理
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean result = activityService.update(activity);

        PrintJson.printJsonFlag(response,result);
    }

    private void getUserListAndAct(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改活动后台");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");
        // 需要返回 一个ulist 一个 Activity
        Map<String,Object> map =activityService.getUserListAndAct(id);
        PrintJson.printJsonObj(response,map);

    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        //删除当前选中的活动，同时删除相关联的remark信息
        System.out.println("进入删除操作流程");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String[] ids = request.getParameterValues("id");

        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityByCondition(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("PageList查询");
        //根据 输入信息进行条件查询
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        pageNum = (pageNum-1) * pageSize;
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        PageListVO<Activity> pageListVO = activityService.PageList(map);
        PrintJson.printJsonObj(response,pageListVO);






    }

    private void saveActivity(HttpServletRequest request, HttpServletResponse response){
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();

        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateBy(createBy);
        activity.setCreateTime(createTime);

        //调用service层进行事务的处理
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean result = activityService.save(activity);

        PrintJson.printJsonFlag(response,result);


    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response){
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
