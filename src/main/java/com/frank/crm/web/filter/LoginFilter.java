package com.frank.crm.web.filter;

import com.frank.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("进行登录验证");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path) || "/login.jsp".equals(path)){
            filterChain.doFilter(request,response);
        }else {
            User user = (User) request.getSession().getAttribute("user");
            if (user==null){
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }else {
                filterChain.doFilter(request,response);
            }
        }

    }
}
