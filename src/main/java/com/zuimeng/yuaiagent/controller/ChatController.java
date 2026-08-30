package com.zuimeng.yuaiagent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    @Data
    static class Study {
        String name;
        String age;
        String sex;
    }
    // 构造的两种方式
    private final ChatModel chatModel;

    private final ChatClient chatClient;

//    public ChatController(ChatModel chatModel, ChatClient.Builder builder) {
//        this.chatModel = chatModel;
//        this.chatClient = builder.defaultSystem("你很幽默").build();
//    }
    @GetMapping("/ai")
    public String getAns(@RequestParam String userInput){
        // 返回的元数据
        ChatResponse chatResponse = chatClient.prompt()
                .user(userInput)
                .call()
                .chatResponse();
        String res = chatResponse.getResult().getOutput().getContent();
        return res;
    }

    @GetMapping("/self-ai")
    public String getAnsBySelf(@RequestParam String userInput) {
        AssistantMessage res = chatModel.call(new Prompt("hello")).getResult().getOutput();
        return res.getContent();
    }

    @GetMapping("/return-entity")
    public void returnEntity() {
        ChatClient ch = ChatClient.builder(chatModel).build();
        ChatClient chatClient1 = ChatClient.create(chatModel);
        // 返回集合对象
        List<Study> list = ch.prompt().user("创建5个学生对象").call().entity(new ParameterizedTypeReference<List<Study>>() {
        });
        // 返回对象
        Study study = chatClient1.prompt().user("小明是一个研究生,15岁，女").call().entity(Study.class);
        System.out.println(list);
        System.out.println(study);
    }

    @GetMapping(value = "/strem-out",produces = "text/html;charset=UTF-8")
    public Flux<String> streamOutput() {
        // 流式响应
        return chatClient.prompt().user("讲一个故事").stream().content();
    }

    @GetMapping("/defaultClient/params")
    public String defaultClientParams(@RequestParam("username") String name, @RequestParam String message) {
        return chatClient.prompt()
                .system(sp -> sp.param("username",name))
                .user(message)
                .call()
                .content();
    }
}
