package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.Tran;

import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Tran getById(String id);

    int updateStageById(Map<String, String> t);
}
