package com.frank.crm.workerbench.service.impl;

import com.frank.crm.utils.DateTimeUtil;
import com.frank.crm.utils.SqlSessionUtil;
import com.frank.crm.utils.UUIDUtil;
import com.frank.crm.workerbench.dao.*;
import com.frank.crm.workerbench.domain.*;
import com.frank.crm.workerbench.service.ClueService;
import sun.misc.UUDecoder;

import java.sql.SQLClientInfoException;
import java.util.List;
import java.util.UUID;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao =
            SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关的DAO
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao =
            SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int count =  clueDao.save(clue);

        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Clue getClueById(String id) {
        Clue clue = clueDao.selectById(id);
        return clue;
    }

    @Override
    public boolean unbind(String id) {
        boolean flag = true;
        int count = clueDao.unbind(id);

        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bindAll(List<ClueActivityRelation> relationList) {
        boolean flag = true;
        for (ClueActivityRelation relation:relationList){
            int count = clueActivityRelationDao.bind(relation);
            if (count!=1){
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag = true;
        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.selectByClueId(clueId);
        // (2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        Customer customer = customerDao.selectByName(clue.getCompany());
        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setCreateBy(createBy);
            customer.setDescription(clue.getDescription());
            customer.setName(clue.getCompany());
            customer.setPhone(clue.getPhone());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setWebsite(clue.getWebsite());
            customer.setAddress(clue.getAddress());
            int count1 = customerDao.save(customer);
            if (count1!=1){
                flag = false;
            }
        }
        // (3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setAddress(clue.getAddress());
        contacts.setAppellation(clue.getAppellation());
        contacts.setCreateBy(createBy);
        contacts.setCreateTime(DateTimeUtil.getSysTime());
        contacts.setCustomerId(customer.getId());
        contacts.setEmail(clue.getEmail());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setFullname(clue.getFullname());
        contacts.setJob(clue.getJob());
        contacts.setSource(clue.getSource());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setNextContactTime(clue.getNextContactTime());
        int count2 = contactsDao.save(contacts);
        if (count2!=1){
            flag = false;
        }
        // (4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getRemarkListByClueId(clue.getId());
        for (ClueRemark remark:clueRemarkList){
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(remark.getNoteContent());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setCreateTime(DateTimeUtil.getSysTime());

            int count3 = customerRemarkDao.save(customerRemark);
            if (count3!=1){
                flag = false;
            }

            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
            contactsRemark.setNoteContent(remark.getNoteContent());
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setEditFlag("0");

            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4!=1){
                flag = false;
            }
        }
        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList =
                clueActivityRelationDao.getRelationListByClueId(clue.getId());
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
            contactsActivityRelation.setActivityId(contactsActivityRelation.getActivityId());
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5!=1){
                flag = false;
            }

        }
        //(6) 如果有创建交易需求，创建一条交易
        if (t!=null){
            int count6 = tranDao.save(t);
            if (count6!=1){
                flag = false;
            }
            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(DateTimeUtil.getSysTime());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setTranId(t.getId());
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setStage(t.getStage());
            int count7 = tranHistoryDao.save(tranHistory);
            if (count7!=1){
                flag = false;
            }
        }

        // (8) 删除线索备注
        int count8 = clueRemarkDao.DeleteByClueId(clue.getId());
        if (count8!=clueRemarkList.size()){
            flag = false;
        }
        // (9) 删除线索和市场活动的关系
        int count9 = clueActivityRelationDao.DeleteByClueId(clue.getId());
        if (count9!=clueActivityRelationList.size()){
            flag = false;
        }
        // (10) 删除线索
        int count10 = clueDao.DeleteById(clue.getId());
        if (count10!=1){
            flag = false;
        }
        return flag;
    }

}
