package com.zuimeng.yuaiagent.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

public class LogAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    private static final Logger logger = LoggerFactory.getLogger(SimpleLoggerAdvisor.class);


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        logRequest(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        logResponse(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        logRequest(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
        return new MessageAggregator().aggregateAdvisedResponse(advisedResponseFlux,this::logResponse);

    }

    @Override
    public String getName() {
        return "日志管理";
    }

    @Override
    public int getOrder() {
        return 0;
    }
    private void logRequest(AdvisedRequest request) {
        logger.debug("request: {}", request);
    }

    private void logResponse(AdvisedResponse chatClientResponse) {
        logger.debug("response: {}", chatClientResponse);
    }
}
