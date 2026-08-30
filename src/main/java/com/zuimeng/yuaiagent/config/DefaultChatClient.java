package com.zuimeng.yuaiagent.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultChatClient {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你是默认的ai助手,每次回复问题，你都加上{username}").build();
    }
}
