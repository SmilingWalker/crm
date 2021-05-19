package com.frank.crm.settings.service;

import com.frank.crm.settings.domain.DictValue;

import java.util.List;
import java.util.Map;

public interface DictionaryService {
    Map<String, List<DictValue>> getAll();
}
