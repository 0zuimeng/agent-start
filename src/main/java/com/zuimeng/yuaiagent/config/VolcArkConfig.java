//package com.zuimeng.yuaiagent.config;
//
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestClient;
//
//@Configuration
//public class VolcArkConfig {
//
//    @Value("${zuimeng.zijie.base-url}")
//    private String baseUrl;
//
//    @Value("${zuimeng.zijie.api-key}")
//    private String apiKey;
//
//    @Value("${zuimeng.zijie.model}")
//    private String model;
//
//    @Bean
//    public OpenAiChatModel chatModel() {
//        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
//
//        return new OpenAiChatModel(
//                openAiApi,
//                org.springframework.ai.openai.OpenAiChatOptions.builder()
//                        .model(model)
//                        .build()
//        );
//    }
//}
