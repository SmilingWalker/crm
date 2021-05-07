package com.frank.crm.settings.web.controller;

import com.frank.crm.settings.dao.UserDao;
import com.frank.crm.settings.domain.User;
import com.frank.crm.settings.service.UserService;
import com.frank.crm.settings.service.impl.UserServiceImpl;
import com.frank.crm.utils.MD5Util;
import com.frank.crm.utils.PrintJson;
import com.frank.crm.utils.ServiceFactory;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入用户控制器");
        String path = request.getServletPath();

        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/settings/user/save.do".equals(path)){

        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response){
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();

        // 将用户名、密码、ip传递给服务层，进行控制
        //验证四种情况
        /**
         * 1. 用户名和密码
         * 2.验证是否失效
         * 3.验证账户是否被锁定
         * 4.验证ip是否在限定内
         *
         * 验证成功，则返回一个user对象，将user对象保存进session内，
         * 同时进行success：true 返回
         *
         *
         * 其次，如果验证失败，进行异常抛出
         * 将异常信息包装进入 msg内，
         * 进行success：false
         * 返回信息
         *
         */

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        try {
            User user = userService.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            String msg = e.getMessage();
            System.out.println(msg);
            Map<String,Object> map = new HashMap<>();
            map.put("msg",msg);
            map.put("success",false);
            PrintJson.printJsonObj(response,map);
        }

    }
}
