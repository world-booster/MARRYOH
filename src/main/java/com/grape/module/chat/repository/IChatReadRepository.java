package com.grape.module.chat.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface IChatReadRepository {

    /*
     * 메시지 읽음 저장
     */
    void insertMessageRead(
            @Param("messageReadKey") String messageReadKey,
            @Param("messageKey") String messageKey,
            @Param("userKey") String userKey
    );

    /*
     * 마지막 읽은 메시지 갱신
     */
    int updateLastReadMessage(
            @Param("roomKey") String roomKey,
            @Param("userKey") String userKey,
            @Param("messageKey") String messageKey
    );

    /*
     * 이미 읽음 처리 여부
     */
    int existMessageRead(
            @Param("messageKey") String messageKey,
            @Param("userKey") String userKey
    );

    String selectLastMessageKey(@Param("roomKey") String roomKey);

    List<String> selectUnreadMessageKeys(@Param("roomKey") String roomKey, @Param("userKey") String userKey);

    int selectReadCount(String messageKey);
}