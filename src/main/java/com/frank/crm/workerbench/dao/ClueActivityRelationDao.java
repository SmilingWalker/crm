package com.frank.crm.workerbench.dao;

import com.frank.crm.workerbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int bind(ClueActivityRelation relation);

    List<ClueActivityRelation> getRelationListByClueId(String id);

    int DeleteByClueId(String id);
}
