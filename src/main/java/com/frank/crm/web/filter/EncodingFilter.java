package com.frank.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        System.out.println("进行字符编码的过滤");

        // post 请求的编码配置
        req.setCharacterEncoding("UTF-8");

        resp.setContentType("text/html;charset=utf-8");

        chain.doFilter(req,resp);

    }
}
