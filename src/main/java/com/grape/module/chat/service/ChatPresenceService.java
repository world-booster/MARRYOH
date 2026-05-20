package com.grape.module.chat.service;

import com.grape.module.chat.model.ChatPresenceDto;
import com.grape.module.chat.repository.IChatPresenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatPresenceService {

    private final IChatPresenceRepository chatPresenceRepository;

    @Transactional
    public void connect(String userKey, String sessionId){
        int existCount = chatPresenceRepository.existPresence(userKey);

        if(existCount > 0){
            chatPresenceRepository.updateOnlinePresence(userKey, sessionId);
            return;
        }

        chatPresenceRepository.insertPresence(userKey, sessionId);
    }

    @Transactional
    public void disconnect(String sessionId){
        chatPresenceRepository.updateOfflinePresence(sessionId);
    }

    @Transactional(readOnly = true)
    public List<ChatPresenceDto> getOnlineUsers(){

        return chatPresenceRepository.selectOnlineUsers();
    }
}
