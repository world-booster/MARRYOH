package com.grape.module.chat.controller;

import com.grape.config.redis.RedisPublisher;
import com.grape.module.chat.event.ChatRedisMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final RedisPublisher redisPublisher;

    private final ChannelTopic chatTopic;

    @MessageMapping("/chat/message")
    public void sendMessage(
            ChatRedisMessage request
    ) {

        redisPublisher.publish(
                chatTopic,
                request
        );
    }
}