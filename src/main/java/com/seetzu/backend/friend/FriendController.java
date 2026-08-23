package com.seetzu.backend.friend;

import com.seetzu.backend.auth.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public List<UserResponse> listFriends(@AuthenticationPrincipal UUID userId) {
        return friendService.listFriends(userId);
    }

    @PostMapping("/requests")
    public FriendRequestResponse sendRequest(
            @AuthenticationPrincipal UUID userId, @Valid @RequestBody SendFriendRequest request) {
        return friendService.sendRequest(userId, request.friendCode());
    }

    @GetMapping("/requests/incoming")
    public List<FriendRequestResponse> listIncomingRequests(@AuthenticationPrincipal UUID userId) {
        return friendService.listIncomingRequests(userId);
    }

    @PostMapping("/requests/{requestId}/accept")
    public void acceptRequest(@AuthenticationPrincipal UUID userId, @PathVariable UUID requestId) {
        friendService.acceptRequest(requestId, userId);
    }

    @PostMapping("/requests/{requestId}/reject")
    public void rejectRequest(@AuthenticationPrincipal UUID userId, @PathVariable UUID requestId) {
        friendService.rejectRequest(requestId, userId);
    }

    public record SendFriendRequest(@NotBlank String friendCode) {}
}
