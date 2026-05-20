package com.grape.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grape.module.chat.event.ChatRedisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try{
            String publishMessage = new String(message.getBody());

            ChatRedisMessage chatMessage = objectMapper.readValue(publishMessage, ChatRedisMessage.class);

            messagingTemplate.convertAndSend("/subscribe/chat/" + chatMessage.getRoomKey(), chatMessage);
        } catch (Exception e){
            log.error("Redis Subscribe Error", e);
        }

    }
}
