package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.FileStoragePolicy;

import java.util.List;

public interface FileStoragePolicyService {

    List<FileStoragePolicy> list();

    FileStoragePolicy getById(Long id);

    Long create(FileStoragePolicy policy);

    void update(Long id, FileStoragePolicy policy);

    void updateEnabled(Long id, Integer enabled);

    void delete(Long id);

    FileStoragePolicy matchPolicy(String fileCategory, String fileType);
}

