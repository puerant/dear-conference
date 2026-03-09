package com.huiwutong.conference.common.aspect;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 权限校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    private final HttpServletRequest request;

    @Before("@annotation(requireLogin)")
    public void checkLogin(RequireLogin requireLogin) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String role = (String) request.getAttribute("role");
        String[] allowedRoles = requireRole.value();
        if (!Arrays.asList(allowedRoles).contains(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
