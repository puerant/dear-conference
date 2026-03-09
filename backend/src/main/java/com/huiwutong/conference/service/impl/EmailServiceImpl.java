package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.service.EmailService;
import com.huiwutong.conference.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 邮件发送服务实现
 * 委托给 EmailTemplateService 完成模板渲染与发送
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateService emailTemplateService;

    @Override
    public void sendVerificationCode(String to, String code) {
        try {
            emailTemplateService.send("VERIFICATION_CODE", to,
                Map.of("code", code, "expireMinutes", "15"));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证码邮件发送失败 to={}", to, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "邮件发送失败，请稍后重试");
        }
    }

    @Override
    public void sendSubmissionConfirmation(String to, String speakerName, String submissionTitle) {
        try {
            emailTemplateService.send("SUBMISSION_CONFIRM", to,
                Map.of("speakerName", speakerName, "submissionTitle", submissionTitle));
        } catch (Exception e) {
            log.warn("投稿确认邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendReviewAssignment(String to, String reviewerName, String submissionTitle) {
        try {
            emailTemplateService.send("REVIEW_ASSIGNMENT", to,
                Map.of("reviewerName", reviewerName, "submissionTitle", submissionTitle));
        } catch (Exception e) {
            log.warn("审稿分配邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendSubmissionResult(String to, String speakerName, String submissionTitle, String result) {
        try {
            String resultColor = "已录用".equals(result) ? "#27ae60" : "#e74c3c";
            emailTemplateService.send("SUBMISSION_RESULT", to,
                Map.of("speakerName", speakerName,
                       "submissionTitle", submissionTitle,
                       "result", result,
                       "resultColor", resultColor));
        } catch (Exception e) {
            log.warn("投稿结果邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendOrderConfirm(String to, String attendeeName, String orderNo,
                                 String ticketName, String amount, int expireMinutes) {
        try {
            emailTemplateService.send("ORDER_CONFIRM", to,
                Map.of("attendeeName", attendeeName,
                       "orderNo", orderNo,
                       "ticketName", ticketName,
                       "amount", amount,
                       "expireMinutes", String.valueOf(expireMinutes)));
        } catch (Exception e) {
            log.warn("订单确认邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendPaymentSuccess(String to, String attendeeName, String orderNo,
                                   String ticketName, String credentialNo) {
        try {
            emailTemplateService.send("PAYMENT_SUCCESS", to,
                Map.of("attendeeName", attendeeName,
                       "orderNo", orderNo,
                       "ticketName", ticketName,
                       "credentialNo", credentialNo));
        } catch (Exception e) {
            log.warn("支付成功邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendCredentialIssued(String to, String attendeeName, String credentialNo,
                                     String seatInfo, String expireDate) {
        try {
            emailTemplateService.send("CREDENTIAL_ISSUED", to,
                Map.of("attendeeName", attendeeName,
                       "credentialNo", credentialNo,
                       "seatInfo", seatInfo,
                       "expireDate", expireDate));
        } catch (Exception e) {
            log.warn("凭证下发邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }

    @Override
    public void sendMeetingReminder(String to, String attendeeName, String conferenceName,
                                    String venue, String conferenceDate) {
        try {
            emailTemplateService.send("MEETING_REMINDER", to,
                Map.of("attendeeName", attendeeName,
                       "conferenceName", conferenceName,
                       "venue", venue,
                       "conferenceDate", conferenceDate));
        } catch (Exception e) {
            log.warn("会议提醒邮件发送失败 to={}, error={}", to, e.getMessage());
        }
    }
}
