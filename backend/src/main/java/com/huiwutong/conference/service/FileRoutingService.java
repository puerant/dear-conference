package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;

public interface FileRoutingService {

    RouteTarget resolve(String fileCategory, String fileType, Long providerId, Long bucketId);

    record RouteTarget(StorageProvider provider, StorageBucket bucket) {
    }
}

