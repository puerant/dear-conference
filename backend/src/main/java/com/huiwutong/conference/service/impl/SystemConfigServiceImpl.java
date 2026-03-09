package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.entity.CredentialConfig;
import com.huiwutong.conference.entity.EmailConfig;
import com.huiwutong.conference.entity.OrderTaskConfig;
import com.huiwutong.conference.entity.PaymentConfig;
import com.huiwutong.conference.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final EmailConfigService emailConfigService;
    private final CredentialConfigService credentialConfigService;
    private final PaymentConfigService paymentConfigService;
    private final OrderTaskConfigService orderTaskConfigService;

    @Override
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> result = new HashMap<>();

        EmailConfig emailConfig = emailConfigService.getPublicConfig();
        if (emailConfig != null) {
            Map<String, Object> emailMap = new HashMap<>();
            emailMap.put("id", emailConfig.getId());
            emailMap.put("host", emailConfig.getHost());
            emailMap.put("port", emailConfig.getPort());
            emailMap.put("username", emailConfig.getUsername());
            emailMap.put("fromName", emailConfig.getFromName());
            emailMap.put("fromEmail", emailConfig.getFromEmail());
            emailMap.put("sslEnabled", emailConfig.getSslEnabled() != null && emailConfig.getSslEnabled() == 1);
            emailMap.put("isEnabled", emailConfig.getIsEnabled() != null && emailConfig.getIsEnabled() == 1);
            result.put("emailConfig", emailMap);
        }

        CredentialConfig credentialConfig = credentialConfigService.getConfig();
        if (credentialConfig != null) {
            Map<String, Object> credentialMap = new HashMap<>();
            credentialMap.put("id", credentialConfig.getId());
            credentialMap.put("templateId", credentialConfig.getTemplateId());
            credentialMap.put("backgroundImage", credentialConfig.getBackgroundImage());
            credentialMap.put("expiryDays", credentialConfig.getExpiryDays());
            result.put("credentialConfig", credentialMap);
        }

        result.put("paymentConfig", paymentConfigService.getList(1, 10).getRecords());

        OrderTaskConfig orderTaskConfig = orderTaskConfigService.getConfig();
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("id", orderTaskConfig.getId());
        taskMap.put("timeoutMinutes", orderTaskConfig.getTimeoutMinutes());
        taskMap.put("checkIntervalMinutes", orderTaskConfig.getCheckIntervalMinutes());
        taskMap.put("cancelTaskEnabled", orderTaskConfig.getCancelTaskEnabled() != null && orderTaskConfig.getCancelTaskEnabled() == 1);
        taskMap.put("refundTaskEnabled", orderTaskConfig.getRefundTaskEnabled() != null && orderTaskConfig.getRefundTaskEnabled() == 1);
        result.put("orderTaskConfig", taskMap);

        return result;
    }
}
