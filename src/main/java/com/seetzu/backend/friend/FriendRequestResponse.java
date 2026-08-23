package com.seetzu.backend.friend;

import com.seetzu.backend.auth.User;
import com.seetzu.backend.auth.UserResponse;
import java.time.Instant;
import java.util.UUID;

public record FriendRequestResponse(UUID id, UserResponse fromUser, Instant createdAt) {
    public static FriendRequestResponse of(FriendRequest request, User fromUser) {
        return new FriendRequestResponse(request.getId(), UserResponse.from(fromUser), request.getCreatedAt());
    }
}
