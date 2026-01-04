package com.finance.moneyowl.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AIChatServiceImpl {

    private final ChatClient chatClient;

    public AIChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                // Register the default system prompt
                .defaultSystem("You are a Professional Financial Assistant in India. " +
                        "You analyze portfolio and Liability data provided by the tools and answer any Portfolio or Liability related queries asked by the User. " +
                        "Always answer in clear, concise markdown format suitable for a mobile app.")
                .defaultTools("getUserPortfolioData", "getUserLiabilities")
                .build();
    }

    public String chat(String userMessage, Long userId) {
        log.info("Start AIChatServiceImpl :: chat - {}", userId);
        return chatClient.prompt()
                .user(u -> u.text(userMessage))
                .system(s -> s.text("Current User ID: " + userId))
                .call()
                .content();
    }
}
