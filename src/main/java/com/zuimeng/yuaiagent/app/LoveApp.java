package com.zuimeng.yuaiagent.app;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class LoveApp {

    private final ChatClient chatClient;

    String sessionId = "001";

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel chatModel) {
        InMemoryChatMemory memory = new InMemoryChatMemory();

        this.chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .defaultSystem(SYSTEM_PROMPT)
                .build();

    }
    public  String doChat(){
        chatClient.prompt().user("hello,my name is xiaoming").advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,sessionId)).call().content();
        chatClient.prompt().user("hello,my name is xiaohong").advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,sessionId)).call().content();
        return chatClient.prompt().user("上面出现了那些人名？").advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,sessionId)).call().content();
    }

}
