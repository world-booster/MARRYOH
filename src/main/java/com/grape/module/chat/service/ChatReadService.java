package com.grape.module.chat.service;

import com.grape.module.chat.model.request.ReadMessageRequest;
import com.grape.module.chat.repository.IChatReadRepository;
import com.grape.module.chat.utils.ChatKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final IChatReadRepository chatReadRepository;

    @Transactional
    public void readMessage(String userKey, ReadMessageRequest request){
        int existCount = chatReadRepository.existMessageRead(request.getMessageKey(), userKey);

        if(existCount > 0){
            return;
        }

        chatReadRepository.insertMessageRead(ChatKeyGenerator.generateMessageReadKey(), request.getMessageKey(), userKey);
        chatReadRepository.updateLastReadMessage(request.getRoomKey(),userKey, request.getMessageKey());
    }
}
