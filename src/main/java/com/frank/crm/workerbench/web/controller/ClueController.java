package com.frank.crm.workerbench.web.controller;

import com.frank.crm.settings.domain.User;
import com.frank.crm.settings.service.UserService;
import com.frank.crm.settings.service.impl.UserServiceImpl;
import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.PrintJson;
import com.frank.crm.utils.ServiceFactory;
import com.frank.crm.utils.UUIDUtil;
import com.frank.crm.vo.PageListVO;
import com.frank.crm.workerbench.domain.*;
import com.frank.crm.workerbench.service.ActivityService;
import com.frank.crm.workerbench.service.ClueService;
import com.frank.crm.workerbench.service.impl.ActivityServiceImpl;
import com.frank.crm.workerbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("线索控制器");
        String path = request.getServletPath();

        if ("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/clue/refreshActList.do".equals(path)){
            refreshActList(request,response);
        }else if ("/workbench/clue/unbind.do".equals(path)){
            unbind(request,response);
        }else if ("/workbench/clue/queryCandidateAct.do".equals(path)){
            queryCandidateAct(request,response);
        }
        else if ("/workbench/clue/bind.do".equals(path)){
            bind(request,response);
        }
        else if ("/workbench/clue/getActByClueId.do".equals(path)){
            getActByClueId(request,response);
        }else if ("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }


    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        Tran t = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getId();
        if ("a".equals(flag)){
            String money = request.getParameter("money");
            String activityId = request.getParameter("activityId");
            String stage = request.getParameter("stage");
            String expectedDate = request.getParameter("expectedDate");
            String name = request.getParameter("name");

            t = new Tran();
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setId(UUIDUtil.getUUID());
            t.setStage(stage);
            t.setMoney(money);
            t.setCreateTime(DateTimeUtil.getSysTime());
            t.setExpectedDate(expectedDate);
            t.setName(name);
        }
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag1 = clueService.convert(clueId,t,createBy);
        if (flag1){
            request.getRequestDispatcher("/workbench/clue/index.jsp").forward(request,response);
        }

    }

    private void getActByClueId(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        String name = request.getParameter("name");
        Map<String,String> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("name",name);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = as.getCandidateActById(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void bind(HttpServletRequest request, HttpServletResponse response) {

        String clueId = request.getParameter("clueId");
        String[] aids = request.getParameterValues("id");
        List<ClueActivityRelation> relationList = new ArrayList<>();
        for(String aid:aids){
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtil.getUUID());
            clueActivityRelation.setActivityId(aid);
            clueActivityRelation.setClueId(clueId);
            relationList.add(clueActivityRelation);
        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bindAll(relationList);
        PrintJson.printJsonFlag(response,flag);
    }

    private void queryCandidateAct(HttpServletRequest request, HttpServletResponse response) {

        String clueId = request.getParameter("clueid");
        String name = request.getParameter("name");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,String> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("name",name);
        List<Activity> activityList = as.getCandidateActList(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void refreshActList(HttpServletRequest request, HttpServletResponse response) {

        //刷新和当前的clue相关的Activity 需要注意的是，返回owner是用户名，同时id是相关的关联关系的id

        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.getActByClueId(id);

        PrintJson.printJsonObj(response,activityList);

    }


    private void unbind(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbind(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue clue = clueService.getClueById(id);

        request.setAttribute("clue",clue);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String stage = request.getParameter("stage");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setStage(stage);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);

        PrintJson.printJsonFlag(response,flag);


    }


    private void getUserList(HttpServletRequest request, HttpServletResponse response){
        System.out.println("获取用户列表");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
