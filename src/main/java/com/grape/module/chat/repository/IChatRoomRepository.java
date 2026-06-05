package com.grape.module.chat.repository;

import com.grape.module.chat.model.ChatRoomDto;
import com.grape.module.chat.model.ChatRoomUserDto;
import com.grape.module.chat.model.response.ChatRoomResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IChatRoomRepository {
    int insertChatRoom(ChatRoomDto chatRoom);
    int insertChatRoomUser(ChatRoomUserDto chatRoomUser);
    int exitRoom(@Param("roomKey") String roomKey, @Param("userKey") String userKey);
    ChatRoomDto selectChatRoom(String roomKey);
    List<ChatRoomDto> selectChatRoomList(String userKey);
    List<ChatRoomResponse> selectChatRoomResponseList(String userKey);
    ChatRoomUserDto selectChatRoomUser(@Param("roomKey") String roomKey, @Param("userKey") String userKey);
    int rejoinChatRoomUser(@Param("roomKey") String roomKey, @Param("userKey") String userKey);

}
