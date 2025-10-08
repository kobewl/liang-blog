package com.liang.blog.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatRequest {
    @NotBlank(message = "用户消息不能为空")
    private String userMessage;
}
