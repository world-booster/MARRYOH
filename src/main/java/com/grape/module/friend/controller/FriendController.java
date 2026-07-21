package com.grape.module.friend.controller;

import com.grape.module.friend.model.FriendRequestCommand;
import com.grape.module.friend.model.FriendSearchResponse;
import com.grape.module.friend.service.FriendService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/search")
    public ResponseEntity<?> searchFriend(
            @RequestParam String type,
            @RequestParam String keyword,
            Authentication authentication
    ){
        String myUserKey = getUserKey(authentication);

        FriendSearchResponse result = friendService.searchFriend(myUserKey, type, keyword);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestFriend(
            @RequestBody FriendRequestCommand command,
            Authentication authentication
    ){
        String myUserKey = getUserKey(authentication);

        friendService.requestFriend(myUserKey, command.getTargetUserKey());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getReceivedRequests(Authentication authentication){
        String myUserKey = getUserKey(authentication);

        return ResponseEntity.ok(friendService.getReceivedRequests(myUserKey));
    }

    @PostMapping("/{friendKey}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable String friendKey,
            Authentication authentication
    ){
        String myUserKey = getUserKey(authentication);

        friendService.acceptRequest(myUserKey, friendKey);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{friendKey}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable String friendKey,
            Authentication authentication
    ){
        String myUserKey = getUserKey(authentication);

        friendService.rejectReqeust(myUserKey, friendKey);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getFriendList(Authentication authentication){
        String myUserKey = getUserKey(authentication);

        return ResponseEntity.ok(friendService.getFriendList(myUserKey));
    }

    private String getUserKey(Authentication authentication){
        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        return user.getUserKey();
    }
}
