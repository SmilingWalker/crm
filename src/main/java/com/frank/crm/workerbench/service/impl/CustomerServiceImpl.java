package com.frank.crm.workerbench.service.impl;

import com.frank.crm.utils.SqlSessionUtil;
import com.frank.crm.workerbench.dao.CustomerDao;
import com.frank.crm.workerbench.dao.CustomerRemarkDao;
import com.frank.crm.workerbench.domain.CustomerRemark;
import com.frank.crm.workerbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    @Override
    public List<String> getNameByParam(String param) {
        List<String> name = customerDao.getNameByParam(param);
        return name;
    }
}
