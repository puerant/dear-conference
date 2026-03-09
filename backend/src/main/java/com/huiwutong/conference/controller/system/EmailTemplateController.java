package com.huiwutong.conference.controller.system;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.EmailTemplate;
import com.huiwutong.conference.service.EmailTemplateService;
import com.huiwutong.conference.service.dto.system.EmailPreviewVo;
import com.huiwutong.conference.service.dto.system.EmailTemplateActionDto;
import com.huiwutong.conference.service.dto.system.UpdateEmailTemplateDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板管理 Controller
 */
@RestController
@RequestMapping("/api/admin/system/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    /** 获取所有场景模板列表（不含 body） */
    @GetMapping
    @RequireRole({"admin"})
    public Result<List<EmailTemplate>> list() {
        return Result.success(emailTemplateService.listAll());
    }

    /** 获取单个模板详情（含 body） */
    @GetMapping("/{sceneCode}")
    @RequireRole({"admin"})
    public Result<EmailTemplate> detail(@PathVariable String sceneCode) {
        return Result.success(emailTemplateService.getBySceneCode(sceneCode));
    }

    /** 更新模板（主题、正文、绑定账号、启用状态） */
    @PutMapping("/{sceneCode}")
    @RequireRole({"admin"})
    public Result<Void> update(@PathVariable String sceneCode,
                               @RequestBody UpdateEmailTemplateDto dto) {
        EmailTemplate template = new EmailTemplate();
        template.setEmailConfigId(dto.getEmailConfigId());
        template.setSubject(dto.getSubject());
        template.setBody(dto.getBody());
        template.setIsEnabled(dto.getIsEnabled());
        emailTemplateService.saveOrUpdate(sceneCode, template);
        return Result.success(null);
    }

    /** 预览模板（渲染占位符，不发送） */
    @PostMapping("/{sceneCode}/preview")
    @RequireRole({"admin"})
    public Result<EmailPreviewVo> preview(@PathVariable String sceneCode,
                                          @RequestBody EmailTemplateActionDto dto) {
        Map<String, String> variables = dto.getVariables() != null ? dto.getVariables() : Map.of();
        return Result.success(emailTemplateService.preview(sceneCode, variables));
    }

    /** 发送测试邮件 */
    @PostMapping("/{sceneCode}/test-send")
    @RequireRole({"admin"})
    public Result<Void> testSend(@PathVariable String sceneCode,
                                 @RequestBody EmailTemplateActionDto dto,
                                 HttpServletRequest request) {
        Map<String, String> variables = dto.getVariables() != null ? dto.getVariables() : Map.of();
        Long userId = (Long) request.getAttribute("userId");
        emailTemplateService.send(sceneCode, dto.getToEmail(), variables, 2, userId);
        return Result.success(null);
    }
}
