package com.liang.blog.controller;

import com.liang.blog.config.AiService;
import com.liang.blog.dto.ai.AiChatRequest;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Validated
public class AiChatController {

    @Resource
    private final AiService aiService;

    public AiChatController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.doChatWithStream(request.getUserMessage());
    }
}
