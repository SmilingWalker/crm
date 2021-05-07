package com.frank.crm.settings.service;


import com.frank.crm.exception.LoginException;
import com.frank.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
