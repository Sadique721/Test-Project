package com.savbill.commonGateway.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketServer extends  AbstractWebSocketMessageBrokerConfigurer{

    @Value("${websocket.gui.endpoint}")
    private String[] urls;

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        registration.taskExecutor(threadPoolTaskExecutor);
        super.configureClientOutboundChannel(registration);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        registration.taskExecutor(threadPoolTaskExecutor);
        super.configureClientInboundChannel(registration);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-endpoint").setAllowedOrigins(urls).withSockJS();
    }
}
