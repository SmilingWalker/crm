package com.frank.crm.workerbench.service;

import com.frank.crm.workerbench.domain.Tran;
import com.frank.crm.workerbench.domain.TranHistory;

public interface TransactionService {
    boolean save(Tran tran, String customerName);

    Tran getById(String id);

    boolean changeStage(String tranId, TranHistory tranHistory);
}
