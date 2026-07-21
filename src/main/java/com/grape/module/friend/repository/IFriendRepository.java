package com.grape.module.friend.repository;

import com.grape.module.friend.model.FriendDto;
import com.grape.module.friend.model.FriendListResponse;
import com.grape.module.friend.model.FriendRequestResponse;
import com.grape.module.friend.model.FriendSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IFriendRepository {

    FriendSearchResponse selectUserByNickname(
            @Param("keyword") String keyword,
            @Param("myUserKey") String myUserKey
    );

    FriendSearchResponse selectUserByEmail(
            @Param("keyword") String keyword,
            @Param("myUserKey") String myUserKey
    );

    FriendDto selectFriendRelation(
            @Param("myUserKey") String myUserKey,
            @Param("targetUserKey") String targetUserKey
    );

    int insertFriendRequest(FriendDto friendDto);

    List<FriendRequestResponse> selectReceivedFriendRequests(
            @Param("myUserKey") String myUserKey
    );

    int acceptFriendRequest(
            @Param("friendKey") String friendKey,
            @Param("myUserKey") String myUserKey
    );

    int rejectFriendRequest(
            @Param("friendKey") String friendKey,
            @Param("myUserKey") String myUserKey
    );

    List<FriendListResponse> selectFriendList(
            @Param("myUserKey") String myUserKey
    );

    int existUser(@Param("userKey") String userKey);
}
