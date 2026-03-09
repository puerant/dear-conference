package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.entity.Dictionary;
import com.huiwutong.conference.entity.DictionaryItem;
import com.huiwutong.conference.mapper.DictionaryItemMapper;
import com.huiwutong.conference.mapper.DictionaryMapper;
import com.huiwutong.conference.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典服务实现
 */
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryMapper dictionaryMapper;
    private final DictionaryItemMapper dictionaryItemMapper;

    @Override
    public List<Dictionary> listAll() {
        return dictionaryMapper.selectList(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getStatus, 1)
                .orderByAsc(Dictionary::getSortOrder));
    }

    @Override
    public List<DictionaryItem> listItemsByCode(String dictCode) {
        Dictionary dict = dictionaryMapper.selectOne(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getDictCode, dictCode)
                .eq(Dictionary::getStatus, 1));
        if (dict == null) {
            return List.of();
        }
        return dictionaryItemMapper.selectList(new LambdaQueryWrapper<DictionaryItem>()
                .eq(DictionaryItem::getDictId, dict.getId())
                .eq(DictionaryItem::getStatus, 1)
                .orderByAsc(DictionaryItem::getSortOrder));
    }

    @Override
    public Dictionary create(Dictionary dictionary) {
        dictionaryMapper.insert(dictionary);
        return dictionary;
    }

    @Override
    public DictionaryItem createItem(DictionaryItem item) {
        dictionaryItemMapper.insert(item);
        return item;
    }
}
