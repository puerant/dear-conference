package com.huiwutong.conference.service;

import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Credential;
import com.huiwutong.conference.service.dto.credential.CredentialQueryDto;

/**
 * 凭证服务接口
 */
public interface CredentialService {

    /**
     * 查询订单凭证（含 userId 归属校验）
     */
    Credential getByOrderId(Long orderId, Long userId);

    Credential generateCredential(Long orderId);

    /**
     * 为团体成员生成入会凭证（幂等）
     */
    Credential generateCredentialForMember(Long orderId, Long groupMemberId);

    void useCredential(String credentialNo);

    void expireByOrderId(Long orderId);

    /**
     * 清除指定团体成员的凭证（将 VALID 凭证置为 EXPIRED）
     */
    void expireByGroupMemberId(Long groupMemberId);

    PageResult<Credential> listAll(CredentialQueryDto query);

    Credential getByCredentialNo(String credentialNo);
}
