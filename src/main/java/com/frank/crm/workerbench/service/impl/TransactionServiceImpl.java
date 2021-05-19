package com.frank.crm.workerbench.service.impl;

import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.SqlSessionUtil;
import com.frank.crm.utils.UUIDUtil;
import com.frank.crm.workerbench.dao.CustomerDao;
import com.frank.crm.workerbench.dao.TranDao;
import com.frank.crm.workerbench.dao.TranHistoryDao;
import com.frank.crm.workerbench.domain.Customer;
import com.frank.crm.workerbench.domain.Tran;
import com.frank.crm.workerbench.domain.TranHistory;
import com.frank.crm.workerbench.service.TransactionService;

import java.util.HashMap;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {
        boolean flag = true;

        // 判断是否有对应的客户，没有则新建
        Customer customer = customerDao.getCustomerByName(customerName);
        if (customer==null){
            customer = new Customer();
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());

            int count = customerDao.save(customer);
            if (count!=1){
                flag = false;
            }
        }

        tran.setCustomerId(customer.getId());

        int count1 = tranDao.save(tran);
        if (count1!=1){
            flag = false;
        }

        TranHistory tranHistory = new TranHistory();
        tranHistory.setStage(tran.getStage());
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setExpectedDate(tran.getExpectedDate());

        int count2 = tranHistoryDao.save(tranHistory);
        if (count2!=1){
            flag = false;
        }
        return flag;

    }

    @Override
    public Tran getById(String id) {
        Tran t = tranDao.getById(id);
        return t;
    }

    @Override
    public boolean changeStage(String tranId,TranHistory tranHistory) {
        boolean flag = true;
        //修改状态
        Map<String,String> map = new HashMap<>();
        map.put("id",tranId);
        map.put("stage",tranHistory.getStage());
        int count0 = tranDao.updateStageById(map);
        if (count0!=1){
            flag = false;
        }


        int count1 = tranHistoryDao.save(tranHistory);
        if (count1!=1){
            flag = false;
        }
        return flag;
    }
}
