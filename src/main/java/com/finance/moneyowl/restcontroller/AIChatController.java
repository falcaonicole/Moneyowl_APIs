package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.service.impl.AIChatServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class AIChatController {

    @Autowired
    private AIChatServiceImpl aiChatService;

    // Request DTO
    public record ChatRequest(String prompt, Long userId) {
    }

    // Response DTO
    public record ChatResponse(String response) {
    }

    @PostMapping
    public ChatResponse handleChat(@RequestBody ChatRequest request) {
        log.info("Start AIChatController :: handleChat - {}", request.userId());
        String aiResponse = aiChatService.chat(request.prompt(), request.userId());
        log.info("End AIChatController :: handleChat");
        return new ChatResponse(aiResponse);
    }
}
