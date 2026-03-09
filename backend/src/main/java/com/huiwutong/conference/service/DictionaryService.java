package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.Dictionary;
import com.huiwutong.conference.entity.DictionaryItem;

import java.util.List;

/**
 * 字典服务接口
 */
public interface DictionaryService {

    List<Dictionary> listAll();

    List<DictionaryItem> listItemsByCode(String dictCode);

    Dictionary create(Dictionary dictionary);

    DictionaryItem createItem(DictionaryItem item);
}
