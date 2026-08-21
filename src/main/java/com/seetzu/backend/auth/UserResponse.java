package com.seetzu.backend.auth;

import java.util.UUID;

public record UserResponse(UUID id, String nickname, String profileImageUrl) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }
}
