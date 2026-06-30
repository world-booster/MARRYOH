package com.grape.module.friend.service;

import com.grape.module.friend.model.FriendDto;
import com.grape.module.friend.model.FriendListResponse;
import com.grape.module.friend.model.FriendReqeustResponse;
import com.grape.module.friend.model.FriendSearchResponse;
import com.grape.module.friend.repository.IFriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final IFriendRepository friendRepository;

    public FriendSearchResponse searchFriend(String myUserKey, String type, String keyword){
        if(keyword == null || keyword.isBlank()){
            throw new IllegalArgumentException("please typing any text on searching box");
        }

        if(type == null || type.isBlank()){
            throw new IllegalArgumentException("please select searching type");
        }

        FriendSearchResponse user;

        if("nickname".equals(type)){
            user = friendRepository.selectUserByNickname(keyword, myUserKey);
        } else if ("email".equals(type)) {
            user = friendRepository.selectUserByEmail(keyword, myUserKey);
        } else {
            throw new IllegalArgumentException("this type is not supported");
        }

        if(user == null){
            return null;
        }

        FriendDto relation = friendRepository.selectFriendRelation(myUserKey, user.getUserKey());
        String friendStatus = resolveFriendStatus(myUserKey, relation);
        user.setFriendStatus(friendStatus);

        return user;
    }

    @Transactional
    public void requestFriend(String myUserKey, String targetUserKey){
        if(targetUserKey == null || targetUserKey.isBlank()){
            throw new IllegalArgumentException("target user is not existed");
        }

        if(myUserKey.equals(targetUserKey)){
            throw new IllegalArgumentException("it is not possible that you request yourself");
        }

        int existUser = friendRepository.existUser(targetUserKey);

        if (existUser == 0){
            throw new IllegalArgumentException("this user is not existed");
        }

        FriendDto relation = friendRepository.selectFriendRelation(myUserKey, targetUserKey);

        if(relation != null){
            if("ACCEPTED".equals(relation.getStatus())){
                throw new IllegalArgumentException("this user is already added on friend list");
            }
            if("REQEUSTED".equals(relation.getStatus())){
                throw new IllegalArgumentException("you are already to requset ");
            }
            if("BLOCKED".equals(relation.getStatus())){
                throw new IllegalArgumentException("you don't request to target user");
            }
        }

        FriendDto friendDto = new FriendDto();
        friendDto.setFriendKey(UUID.randomUUID().toString());
        friendDto.setRequestUserKey(myUserKey);
        friendDto.setTargetUserKey(targetUserKey);
        friendDto.setStatus("REQEUSTED");

        friendRepository.insertFriendRequest(friendDto);
    }

    public List<FriendReqeustResponse> getReceivedRequests(String myUserKey){
        return friendRepository.selectReceivedFriendRequests(myUserKey);
    }

    @Transactional
    public void acceptRequest(String myUserKey, String friendKey){
        int result = friendRepository.acceptFriendRequest(friendKey, myUserKey);

        if(result == 0){
            throw new IllegalArgumentException("you can not accept this request");
        }
    }

    @Transactional
    public void rejectReqeust(String myUserKey, String friendKey){
        int result = friendRepository.rejectFriendRequest(friendKey, myUserKey);

        if(result == 0){
            throw new IllegalArgumentException("you can not reject this request");
        }
    }

    public List<FriendListResponse> getFriendList(String myUserKey){
        return friendRepository.selectFriendList(myUserKey);
    }

    private String resolveFriendStatus(String myUserKey, FriendDto relation){

        if(relation == null){
            return "NONE";
        }

        if("ACCEPTED".equals(relation.getStatus())){
            return "ACCEPTED";
        }

        if("REQUESTED".equals(relation.getStatus())){
            if(myUserKey.equals(relation.getRequestUserKey())){
                return "REQUESTED";
            }

            return "RECEIVED";
        }

        if("REJECTED".equals(relation.getStatus())){
            return "NONE";
        }

        if("BLOCKED".equals(relation.getStatus())){
            return "BLOCKED";
        }

        return "NONE";
    }
}
