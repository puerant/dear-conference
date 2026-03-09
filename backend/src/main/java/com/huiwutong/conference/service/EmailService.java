package com.huiwutong.conference.service;

/**
 * 邮件发送服务 —— 业务层统一入口
 * 实现层委托给 EmailTemplateService 完成渲染与发送
 */
public interface EmailService {

    void sendVerificationCode(String to, String code);

    void sendSubmissionConfirmation(String to, String speakerName, String submissionTitle);

    void sendReviewAssignment(String to, String reviewerName, String submissionTitle);

    void sendSubmissionResult(String to, String speakerName, String submissionTitle, String result);

    void sendOrderConfirm(String to, String attendeeName, String orderNo,
                          String ticketName, String amount, int expireMinutes);

    void sendPaymentSuccess(String to, String attendeeName, String orderNo,
                            String ticketName, String credentialNo);

    void sendCredentialIssued(String to, String attendeeName, String credentialNo,
                              String seatInfo, String expireDate);

    void sendMeetingReminder(String to, String attendeeName, String conferenceName,
                             String venue, String conferenceDate);
}
