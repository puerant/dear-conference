package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.StorageProvider;

import java.util.List;

public interface StorageProviderService {

    List<StorageProvider> list();

    StorageProvider getById(Long id);

    Long create(StorageProvider provider);

    void update(Long id, StorageProvider provider);

    void updateEnabled(Long id, Integer enabled);

    void setDefault(Long id);

    void delete(Long id);

    void testConnection(Long id);

    StorageProvider getDefaultEnabledProvider();
}

