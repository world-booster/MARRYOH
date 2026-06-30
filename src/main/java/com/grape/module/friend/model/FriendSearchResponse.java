package com.grape.module.friend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendSearchResponse {
        private String userKey;
        private String userNickname;
        private String userEmail;

        /*
        * NONE      : possible to request
        * REQUESTED : already to request
        * RECEIVED  : requested by anyone
        * ACCEPTED  : friend
        * */
        private String friendStatus;
}
