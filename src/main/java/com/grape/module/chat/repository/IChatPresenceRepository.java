package com.grape.module.chat.repository;

import com.grape.module.chat.model.ChatPresenceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IChatPresenceRepository {

    /*
     * Presence 존재 여부
     */
    int existPresence(String userKey);

    /*
     * Presence 생성
     */
    int insertPresence(
            @Param("userKey") String userKey,
            @Param("sessionId") String sessionId
    );

    /*
     * 온라인 상태 변경
     */
    int updateOnlinePresence(
            @Param("userKey") String userKey,
            @Param("sessionId") String sessionId
    );

    /*
     * 오프라인 상태 변경
     */
    int updateOfflinePresence(
            @Param("sessionId") String sessionId
    );

    /*
     * 현재 접속 유저 조회
     */
    List<ChatPresenceDto> selectAllPresenceUsers();

    void updateCurrentRoom(@Param("userKey") String userKey, @Param("roomKey") String roomKey);
    void clearCurrentRoom(@Param("userKey") String userKey);
    int countUsersInRoom(@Param("roomKey") String roomKey);
}