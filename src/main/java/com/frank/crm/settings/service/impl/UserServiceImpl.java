package com.frank.crm.settings.service.impl;

import com.frank.crm.exception.LoginException;
import com.frank.crm.settings.dao.UserDao;
import com.frank.crm.settings.domain.User;
import com.frank.crm.settings.service.UserService;
import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.SqlSessionUtil;
import org.omg.CORBA.UserException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        // 将信息封装为 map对象，并对返回值进行判断
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        map.put("ip",ip);
        User user = userDao.login(map);

        /**
         * 1. 用户名和密码
         * 2.验证是否失效
         * 3.验证账户是否被锁定
         * 4.验证ip是否在限定内
         **/
        if (user==null){
            //用户名或密码错误，返回为空
            throw new LoginException("用户名或密码错误");
        }else {

            String sysTime = DateTimeUtil.getSysTime();
            String expireTime = user.getExpireTime();
            if (sysTime.compareTo(expireTime)>0){
                throw new LoginException("当前账户已经失效");
            }
            if ("0".equals(user.getLockState())){
                throw new LoginException("当前账户被锁定");
            }
            if (!user.getAllowIps().contains(ip)){
                throw new LoginException("当前Ip不在允许范围内");
            }
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }
}
