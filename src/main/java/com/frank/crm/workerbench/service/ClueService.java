package com.frank.crm.workerbench.service;

import com.frank.crm.workerbench.domain.Clue;
import com.frank.crm.workerbench.domain.ClueActivityRelation;
import com.frank.crm.workerbench.domain.Tran;

import java.util.List;

public interface ClueService {
    boolean save(Clue clue);

    Clue getClueById(String id);

    boolean unbind(String id);

    boolean bindAll(List<ClueActivityRelation> relationList);

    boolean convert(String clueId, Tran t, String createBy);
}
