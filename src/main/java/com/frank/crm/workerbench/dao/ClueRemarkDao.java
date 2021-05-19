package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getRemarkListByClueId(String id);

    int DeleteByClueId(String id);
}
