package com.grape.module.chat.service;

import com.grape.module.chat.model.SocketEvent;
import com.grape.module.chat.model.request.ReadMessageRequest;
import com.grape.module.chat.model.response.ChatSocketMessageResponse;
import com.grape.module.chat.repository.IChatReadRepository;
import com.grape.module.chat.utils.ChatKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final IChatReadRepository chatReadRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void readMessage(String userKey, ReadMessageRequest request){

        List<String> unreadMessages = chatReadRepository.selectUnreadMessageKeys(request.getRoomKey(), userKey);

        for(String messageKey : unreadMessages){
            int existCount = chatReadRepository.existMessageRead(messageKey, userKey);

            if(existCount > 0){
                continue;
            }

            chatReadRepository.insertMessageRead(ChatKeyGenerator.generateMessageReadKey(), messageKey, userKey);
            int readCount = chatReadRepository.selectReadCount(messageKey);

            ChatSocketMessageResponse payload =
                    ChatSocketMessageResponse.builder()
                            .messageKey(messageKey)
                            .roomKey(request.getRoomKey())
                            .readCount(readCount)
                            .build();

            SocketEvent<ChatSocketMessageResponse> event =
                    SocketEvent.<ChatSocketMessageResponse>builder()
                            .eventType("READ")
                            .payload(payload)
                            .build();

            messagingTemplate.convertAndSend("/subscribe/chat/" + request.getRoomKey(),
                    event);
        }

        if(request.getMessageKey() != null){

            chatReadRepository.updateLastReadMessage(request.getRoomKey(), userKey, request.getMessageKey());
        }
    }
}
