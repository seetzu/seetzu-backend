package com.seetzu.backend.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** 카카오 https://kapi.kakao.com/v2/user/me 응답 중 우리가 쓰는 필드만. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfo(Long id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {

    public String nickname() {
        return profile() != null ? profile().nickname() : null;
    }

    public String profileImageUrl() {
        return profile() != null ? profile().profileImageUrl() : null;
    }

    public String email() {
        return kakaoAccount != null ? kakaoAccount.email() : null;
    }

    private Profile profile() {
        return kakaoAccount != null ? kakaoAccount.profile() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(Profile profile, String email) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(String nickname, @JsonProperty("profile_image_url") String profileImageUrl) {}
}
