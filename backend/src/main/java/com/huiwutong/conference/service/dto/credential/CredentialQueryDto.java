package com.huiwutong.conference.service.dto.credential;

import lombok.Data;

/**
 * 凭证查询 DTO
 */
@Data
public class CredentialQueryDto {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String credentialNo;
    private String status;
}
