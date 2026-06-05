package com.grape.module.chat.service;

import com.grape.module.chat.model.ChatMessageDto;
import com.grape.module.chat.model.SocketEvent;
import com.grape.module.chat.model.request.ChatSocketMessageRequest;
import com.grape.module.chat.model.response.ChatSocketMessageResponse;
import com.grape.module.chat.repository.IChatMessageRepository;
import com.grape.module.chat.model.request.ChatMessageRequest;
import com.grape.module.chat.model.response.ChatMessageResponse;
import com.grape.module.chat.utils.ChatKeyGenerator;
import com.grape.module.user.adaptor.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final IChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatPresenceService chatPresenceService;
    private final UserPort userPort;

    @Transactional
    public ChatMessageResponse sendMessage(
            String userKey,
            ChatMessageRequest request
    ) {

        String messageKey = ChatKeyGenerator.generateMessageKey();
        String nickname = userPort.selectUserNickname(userKey);

        ChatMessageDto chatMessage = ChatMessageDto.builder()
                .messageKey(messageKey)
                .roomKey(request.getRoomKey())
                .messageType(request.getMessageType())
                .senderUserKey(userKey)
                .messageContent(request.getMessageContent())
                .fileKey(request.getFileKey())
                .senderNickname(nickname)
                .createdAt(LocalDateTime.now())
                .build();

        /*
         * 메시지 저장
         */
        chatMessageRepository.insertMessage(chatMessage);

        /*
         * 채팅방 마지막 메시지 갱신
         */
        chatMessageRepository.updateLastMessage(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.builder()
                .messageKey(chatMessage.getMessageKey())
                .roomKey(chatMessage.getRoomKey())
                .senderUserKey(chatMessage.getSenderUserKey())
                .messageType(chatMessage.getMessageType())
                .messageContent(chatMessage.getMessageContent())
                .fileKey(chatMessage.getFileKey())
                .createdAt(chatMessage.getCreatedAt())
                .senderNickname(chatMessage.getSenderNickname())
                .build();

        /*
         * WebSocket Broadcast
         */

        int totalUsers = chatMessageRepository.countRoomUsers(chatMessage.getRoomKey());
        int activeUsers = chatPresenceService.countUsersInRoom(chatMessage.getRoomKey());
        int unreadCount = Math.max(totalUsers - activeUsers, 0);

        ChatSocketMessageResponse socketPayload =
                ChatSocketMessageResponse.builder()
                        .messageKey(chatMessage.getMessageKey())
                        .roomKey(chatMessage.getRoomKey())
                        .senderUserKey(chatMessage.getSenderUserKey())
                        .messageType(chatMessage.getMessageType())
                        .messageContent(chatMessage.getMessageContent())
                        .fileKey(chatMessage.getFileKey())
                        .createdAt(chatMessage.getCreatedAt())
                        .senderNickname(chatMessage.getSenderNickname())
                        .readCount(unreadCount)
                        .build();

        SocketEvent<ChatSocketMessageResponse> socketEvent =
                SocketEvent.<ChatSocketMessageResponse>builder()
                        .eventType("MESSAGE")
                        .payload(socketPayload)
                        .build();

        messagingTemplate.convertAndSend(
                "/subscribe/chat/" + chatMessage.getRoomKey(),
                socketEvent
        );

        return response;
    }

    @Transactional
    public void sendSystemMessage(String roomKey, String userKey, String content){
        String messageKey = ChatKeyGenerator.generateMessageKey();

        ChatMessageDto chatMessage = ChatMessageDto.builder()
                .messageKey(messageKey)
                .roomKey(roomKey)
                .messageType("SYSTEM")
                .senderUserKey(userKey)
                .messageContent(content)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.insertMessage(chatMessage);
        chatMessageRepository.updateLastMessage(chatMessage);

        ChatSocketMessageResponse socketPayload = ChatSocketMessageResponse.builder()
                .messageKey(chatMessage.getMessageKey())
                .roomKey(chatMessage.getRoomKey())
                .senderUserKey(chatMessage.getSenderUserKey())
                .messageType(chatMessage.getMessageType())
                .messageContent(chatMessage.getMessageContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();

        SocketEvent<ChatSocketMessageResponse> socketEvent =
                SocketEvent.<ChatSocketMessageResponse>builder()
                        .eventType("MESSAGE")
                        .payload(socketPayload)
                        .build();

        messagingTemplate.convertAndSend("/subscribe/chat/" + roomKey, socketEvent);
    }

    @Transactional
    public ChatSocketMessageResponse sendSocketMessage(ChatSocketMessageRequest request){
        String messageKey = ChatKeyGenerator.generateMessageKey();
        String nickname = userPort.selectUserNickname(request.getSenderUserKey());

        ChatMessageDto chatMessage = ChatMessageDto.builder()
                .messageKey(messageKey)
                .roomKey(request.getRoomKey())
                .messageType(request.getMessageType())
                .senderUserKey(request.getSenderUserKey())
                .messageContent(request.getMessageContent())
                .fileKey(request.getFileKey())
                .senderNickname(nickname)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.insertMessage(chatMessage);

        chatMessageRepository.updateLastMessage(chatMessage);

        return ChatSocketMessageResponse.builder()
                .messageKey(chatMessage.getMessageKey())
                .roomKey(chatMessage.getRoomKey())
                .senderUserKey(chatMessage.getSenderUserKey())
                .messageType(chatMessage.getMessageType())
                .messageContent(chatMessage.getMessageContent())
                .fileKey(chatMessage.getFileKey())
                .createdAt(chatMessage.getCreatedAt())
                .senderNickname(chatMessage.getSenderNickname())
                .readCount(chatMessage.getReadCount())
                .build();
    }

    @Transactional
    public List<ChatMessageResponse> getMessageList(String roomKey){
        List<ChatMessageDto> messageList = chatMessageRepository.selectMessageList(roomKey);

        return messageList.stream()
                .map(message -> ChatMessageResponse.builder()
                        .messageKey(message.getMessageKey())
                        .roomKey(message.getRoomKey())
                        .senderUserKey(message.getSenderUserKey())
                        .messageType(message.getMessageType())
                        .messageContent(message.getMessageContent())
                        .fileKey(message.getFileKey())
                        .createdAt(message.getCreatedAt())
                        .senderNickname(message.getSenderNickname())
                        .readCount(message.getReadCount())
                        .build())
                .toList();
    }

    @Transactional
    public List<ChatMessageResponse> getHistory(String roomKey, LocalDateTime before){
        return chatMessageRepository.getHistory(roomKey, before);
    }

}