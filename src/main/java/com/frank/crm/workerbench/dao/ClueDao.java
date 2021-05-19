package com.frank.crm.workerbench.dao;


import com.frank.crm.workerbench.domain.Clue;

public interface ClueDao {


    Clue selectByClueId(String clueId) ;

    int save(Clue clue);

    Clue selectById(String id);

    int unbind(String id);

    int DeleteById(String id);
}
