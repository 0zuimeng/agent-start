# 学习记录
## springAI
### 依赖
```angular2html
<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.ai</groupId>
				<artifactId>spring-ai-bom</artifactId>
				<version>1.0.0-M5</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```
注：这是1.x.x版本，2.x.x版见官网，注还有快照版本
### 客户端
#### 创建的两重方式
```angular2html
1.chatClient
private final ChatClient chatClient;
public ChatController(ChatClient.Builder builder){
this.chatClient = builder.defaultSystem("你是一个懒惰的AI助手，无论你收到：什么信息，你都会出于懒惰，回答：不知道").build();
}
chatClient.prompt()
          .user(userInput)
          .call()
          .content();
2.chatModel
private final ChatClient chatClient;
AssistantMessage res = chatModel.call(new Prompt("hello")).getResult().getOutput();
return res.getContent();
3.chatModel -> chatClient
ChatClient.builder(chatModel).build();
ChatClient.create(chatModel)
```
#### 创建多个 ChatClient 实例
1. 同一模型
默认情况下，Spring AI 会自动配置一个 ChatClient.Builder bean。不过，您的应用程序中可能需要使用多个聊天模型。以下是处理这种情况的方法：
在所有情况下，都需要通过设置属性 spring.ai.chat.client.enabled=false 来禁用 ChatClient.Builder 的自配置功能。
示例：
```angular2html
// Create ChatClient instances programmatically
ChatModel myChatModel = ... // already autoconfigured by Spring Boot
ChatClient chatClient = ChatClient.create(myChatModel);

// Or use the builder for more control
ChatClient.Builder builder = ChatClient.builder(myChatModel);
ChatClient customChatClient = builder
    .defaultSystemPrompt("You are a helpful assistant.")
    .build();
```
2. 不同模型
为每个模型定义独立的 ChatClient 组件：
 ```@Configuration
   public class ChatClientConfig {

   @Bean
   public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
   return ChatClient.create(chatModel);
   }

   @Bean
   public ChatClient anthropicChatClient(AnthropicChatModel chatModel) {
   return ChatClient.create(chatModel);
   }
   }
 ```
#### 
## postgresql
### 依赖
```angular2html
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```