package com.huiwutong.conference.controller;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Dictionary;
import com.huiwutong.conference.entity.DictionaryItem;
import com.huiwutong.conference.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典控制器（公开接口）
 */
@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping
    public Result<List<Dictionary>> listAll() {
        return Result.success(dictionaryService.listAll());
    }

    @GetMapping("/{dictCode}/items")
    public Result<List<DictionaryItem>> listItems(@PathVariable String dictCode) {
        return Result.success(dictionaryService.listItemsByCode(dictCode));
    }
}
