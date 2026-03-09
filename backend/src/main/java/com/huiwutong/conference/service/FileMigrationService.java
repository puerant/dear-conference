package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.FileMigrationTask;

import java.util.List;

public interface FileMigrationService {

    Long createTask(Long sourceProviderId, Long targetProviderId, Long createdBy);

    List<FileMigrationTask> list();

    FileMigrationTask getById(Long id);

    void retry(Long id);
}

