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
#### 返回响应的格式及方式
1.返回元数据 包含消耗token数
```angular2html
ChatResponse chatResponse = chatClient.prompt()
    .user("Tell me a joke")
    .call()
    .chatResponse();
```
2.实体返回
(1)实体对象
record ActorFilms(String actor, List<String> movies) {}
record,class都可以
```angular2html
ActorFilms actorFilms = chatClient.prompt()
.user("Generate the filmography for a random actor.")
.call()
.entity(ActorFilms.class);
```
(2)集合
```angular2html
List<ActorFilms> actorFilms = chatClient.prompt()
    .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
    .call()
    .entity(new ParameterizedTypeReference<List<ActorFilms>>() {});
```
3.流式返回
```angular2html
Flux<String> output = chatClient.prompt()
    .user("Tell me a joke")
    .stream()
    .content();
```
####  在流畅的 API 中使用参数
```angular2html
var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorsFilms>>() {});

Flux<String> flux = this.chatClient.prompt()
    .user(u -> u.text("""
                        Generate the filmography for a random actor.
                        {format}
                      """)
            .param("format", this.converter.getFormat()))
    .stream()
    .content();

String content = this.flux.collectList().block().stream().collect(Collectors.joining());

List<ActorsFilms> actorFilms = this.converter.convert(this.content);
```
#### Prompt Templates  提示模板
```angular2html
String answer = ChatClient.create(chatModel).prompt()
    .user(u -> u
            .text("Tell me the names of 5 movies whose soundtrack was composed by {composer}")
            .param("composer", "John Williams"))
    .call()
    .content();
```
#### call() 方法之后的返回值
1. String content() ：返回响应的字符串内容
2. ChatResponse chatResponse() ：返回包含多代数据以及关于响应信息的元数据的 ChatResponse 对象。例如，用于生成该响应的令牌数量等信息。
3. ChatClientResponse chatClientResponse() ：返回一个 ChatClientResponse 对象，该对象包含 ChatResponse 对象和 ChatClient 执行上下文。通过这种方式，你可以访问在执行顾问过程中所使用的额外数据（例如，在 RAG 流程中检索到的相关文档）。
4. ResponseEntity<?> responseEntity() ：返回一个包含完整 HTTP 响应的 ResponseEntity ，其中包括状态码、头部信息以及响应体内容。当你需要获取响应的低级 HTTP 细节时，这个工具非常有用。
5. entity() 用于返回 Java 类型
   entity(ParameterizedTypeReference<T> type) ：用于返回指定数量的实体类型。
   entity(Class<T> type) ：用于返回特定类型的实体。
   entity(StructuredOutputConverter<T> structuredOutputConverter) ：用于指定一个 StructuredOutputConverter 的实例，将该 String 转换为特定类型的实体。
   调用 call() 方法实际上并不会触发 AI 模型的执行。相反，它只是告诉 Spring AI 是采用同步调用还是流式调用方式。真正的 AI 模型调用发生在如 content() 、 chatResponse() 和 responseEntity() 这样的方法被调用时。
#### stream()方法之后的返回值
1. Flux<String> content() ：返回由 AI 模型生成的字符串中的 Flux 内容。
2. Flux<ChatResponse> chatResponse() ：返回 ChatResponse 对象中的 Flux 信息，该信息包含了关于响应的额外元数据。
3. Flux<ChatClientResponse> chatClientResponse() ：返回 ChatClientResponse 对象中的 ChatResponse 对象，该对象包含了 ChatClient 的执行上下文。通过这种方式，你可以访问在顾问执行过程中所使用的其他数据，例如在 RAG 流程中检索到的相关文档。
#### 配置默认客户端
1. 默认系统文本
```
1.创建配置
@Configuration
class Config {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot that answers question in the voice of a Pirate")
                .build();
    }

}
2. 调用
@RestController
class AIController {

	private final ChatClient chatClient;

	AIController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping("/ai/simple")
	public Map<String, String> completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return Map.of("completion", this.chatClient.prompt().user(message).call().content());
	}
}
```
2. 带有参数的默认系统文本
```
@Configuration
class Config {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .build();
    }

}
this.chatClient.prompt()
						.system(sp -> sp.param("voice", voice))
						.user(message)
						.call()
						.content());
```
## postgresql
### 依赖
```angular2html
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```