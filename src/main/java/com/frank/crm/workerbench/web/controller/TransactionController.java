package com.frank.crm.workerbench.web.controller;

import com.frank.crm.settings.domain.User;
import com.frank.crm.settings.service.UserService;
import com.frank.crm.settings.service.impl.UserServiceImpl;
import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.PrintJson;
import com.frank.crm.utils.ServiceFactory;
import com.frank.crm.utils.UUIDUtil;
import com.frank.crm.workerbench.domain.*;
import com.frank.crm.workerbench.service.ActivityService;
import com.frank.crm.workerbench.service.ClueService;
import com.frank.crm.workerbench.service.CustomerService;
import com.frank.crm.workerbench.service.TransactionService;
import com.frank.crm.workerbench.service.impl.ActivityServiceImpl;
import com.frank.crm.workerbench.service.impl.ClueServiceImpl;
import com.frank.crm.workerbench.service.impl.CustomerServiceImpl;
import com.frank.crm.workerbench.service.impl.TransactionServiceImpl;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class TransactionController extends HttpServlet {


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("线索控制器");
        String path = request.getServletPath();

        if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request, response);
        } else if ("/workbench/transaction/add.do".equals(path)) {
            getUserList(request, response);
        }else if ("/workbench/transaction/save.do".equals(path)) {
            save(request, response);
        }else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request, response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request, response);
        }
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");

        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(stage);
        tranHistory.setTranId(id);
        tranHistory.setExpectedDate(expectedDate);
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setMoney(money);
        tranHistory.setCreateBy(((User)request.getSession().getAttribute("user")).getName());

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean flag = ts.changeStage(id,tranHistory);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("tranHistory",tranHistory);
        PrintJson.printJsonObj(response,map);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");


        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());

        ServletContext application = request.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        Tran t = ts.getById(id);

        t.setPossibility(pMap.get(t.getStage()));
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession().getAttribute("user")).getId();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setCreateTime(createTime);
        tran.setMoney(money);
        tran.setStage(stage);
        tran.setCreateBy(createBy);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setDescription(description);
        tran.setSource(source);
        tran.setType(type);
        tran.setOwner(owner);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TransactionService transactionService = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean flag = transactionService.save(tran,customerName);

        if (flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }else {
            PrintJson.printJsonFlag(response,false);
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        String param = request.getParameter("name");
        List<String> names =  customerService.getNameByParam(param);

        PrintJson.printJsonObj(response,names);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("获取用户列表");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("ulist",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }

}
