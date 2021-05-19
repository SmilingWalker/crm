package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer selectByName(String company);

    int save(Customer customer);

    List<String> getNameByParam(String param);

    Customer getCustomerByName(String customerName);
}
