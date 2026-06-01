package com.grape.module.chat.service;

import com.grape.module.chat.model.ChatRoomDto;
import com.grape.module.chat.model.ChatRoomInviteEventDto;
import com.grape.module.chat.model.SocketEvent;
import com.grape.module.chat.model.request.ChatRoomRequest;
import com.grape.module.chat.model.response.ChatRoomResponse;
import com.grape.module.chat.model.ChatRoomUserDto;
import com.grape.module.chat.repository.IChatRoomRepository;
import com.grape.module.chat.utils.ChatKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final IChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatRoomResponse createChatRoom(String loginUserKey, ChatRoomRequest request){
        String roomKey = ChatKeyGenerator.generateRoomKey();

        ChatRoomDto chatRoom = ChatRoomDto.builder()
                .roomKey(roomKey)
                .roomName(request.getRoomName())
                .roomType(request.getRoomType())
                .createdUserKey(loginUserKey)
                .build();

        chatRoomRepository.insertChatRoom(chatRoom);

        /*
         * 생성자 자동 참여
         */
        ChatRoomUserDto owner = ChatRoomUserDto.builder()
                .roomUserKey(
                        ChatKeyGenerator.generateRoomUserKey()
                )
                .roomKey(roomKey)
                .userKey(loginUserKey)
                .build();

        chatRoomRepository.insertChatRoomUser(owner);

        /*
         * 초대 유저 추가
         */
        for(String userKey : request.getUserKeys()) {

            /*
             * 방장 중복 방지
             */
            if(userKey.equals(loginUserKey)){
                continue;
            }

            ChatRoomUserDto roomUser =
                    ChatRoomUserDto.builder()
                            .roomUserKey(
                                    ChatKeyGenerator.generateRoomUserKey()
                            )
                            .roomKey(roomKey)
                            .userKey(userKey)
                            .build();

            chatRoomRepository.insertChatRoomUser(roomUser);
        }

        ChatRoomResponse response = ChatRoomResponse.builder()
                .roomKey(chatRoom.getRoomKey())
                .roomName(chatRoom.getRoomName())
                .roomType(chatRoom.getRoomType())
                .createdAt(chatRoom.getCreatedAt())
                .build();

        List<String> targetUsers = new ArrayList<>();
        targetUsers.add(loginUserKey);
        for (String userKey : request.getUserKeys()){
            if(!targetUsers.contains(userKey)){
                targetUsers.add(userKey);
            }
        }

        SocketEvent<ChatRoomResponse> socketEvent = SocketEvent.<ChatRoomResponse>builder()
                .eventType("ROOM_CREATED")
                .payload(response)
                .build();

        for(String userKey : targetUsers){
            messagingTemplate.convertAndSend(
                    "/subscribe/users/" + userKey,
                    socketEvent
            );
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomList(String userKey){
        return chatRoomRepository.selectChatRoomResponseList(userKey);
    }

    @Transactional
    public void inviteUsers(String roomKey, List<String> userKeys){

        ChatRoomDto room = chatRoomRepository.selectChatRoom(roomKey);

        for(String userKey : userKeys){
            ChatRoomUserDto roomUser =
                    ChatRoomUserDto.builder()
                            .roomUserKey(
                                    ChatKeyGenerator
                                            .generateRoomUserKey()
                            )
                            .roomKey(roomKey)
                            .userKey(userKey)
                            .build();

            chatRoomRepository.insertChatRoomUser(roomUser);

            ChatRoomInviteEventDto event = ChatRoomInviteEventDto.builder()
                    .roomKey(room.getRoomKey())
                    .roomName(room.getRoomName())
                    .roomType(room.getRoomType())
                    .build();

            SocketEvent<ChatRoomInviteEventDto> socketEvent =
                    SocketEvent.<ChatRoomInviteEventDto>builder()
                            .eventType("ROOM_INVITE")
                            .payload(event)
                            .build();

            messagingTemplate.convertAndSend(
                    "/subscribe/users/" + userKey,
                    socketEvent
            );
        }
    }

    @Transactional
    public void exitRoom(String roomKey, String userKey){
        chatRoomRepository.exitRoom(roomKey, userKey);
    }
}
