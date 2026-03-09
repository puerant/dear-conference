package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.StorageBucket;

import java.util.List;

public interface StorageBucketService {

    List<StorageBucket> list(Long providerId);

    StorageBucket getById(Long id);

    Long create(StorageBucket bucket);

    void update(Long id, StorageBucket bucket);

    void updateEnabled(Long id, Integer enabled);

    void setDefault(Long id);

    void delete(Long id);

    StorageBucket getDefaultEnabledBucketByProvider(Long providerId);
}

