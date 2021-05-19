package com.frank.crm.settings.dao;

import com.frank.crm.settings.domain.DictValue;

import java.util.List;

public interface DictValueDao {
    List<DictValue> getValueByCode(String code);
}
