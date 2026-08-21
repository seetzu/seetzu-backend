package com.seetzu.backend.auth;

public record AuthResponse(String accessToken, UserResponse user) {}
