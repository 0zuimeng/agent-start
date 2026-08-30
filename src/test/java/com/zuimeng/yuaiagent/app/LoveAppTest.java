package com.zuimeng.yuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoveApp 集成测试：验证对话记忆功能
 */
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChatWithMemory() {
        String result = loveApp.doChat();
        // 有返回内容
        assertNotNull(result);
        // 记忆生效：模型应能回忆起前两次对话中出现的人名
        assertTrue(result.contains("xiaoming"), "记忆中应包含 xiaoming，实际返回：" + result);
        assertTrue(result.contains("xiaohong"), "记忆中应包含 xiaohong，实际返回：" + result);
        System.out.println(result);
    }
}
