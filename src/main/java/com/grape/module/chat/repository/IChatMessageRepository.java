package com.grape.module.chat.repository;

import com.grape.module.chat.model.ChatMessageDto;
import com.grape.module.chat.model.response.ChatMessageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface IChatMessageRepository {

    /*
     * 메시지 저장
     */
    int insertMessage(ChatMessageDto chatMessage);

    /*
     * 메시지 목록 조회
     */
    List<ChatMessageDto> selectMessageList(String roomKey);

    /*
     * 마지막 메시지 갱신
     */
    int updateLastMessage(ChatMessageDto chatMessage);

    int countRoomUsers(String roomKey);

    List<ChatMessageResponse> getHistory(@Param("roomKey") String roomKey, @Param("before")LocalDateTime before);
}