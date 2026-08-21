package com.seetzu.backend.auth;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * 플러터 앱이 카카오 SDK로 직접 로그인해서 얻은 카카오 액세스 토큰을 받아, 그 토큰으로
 * 카카오 사용자 정보 API를 호출해 신원을 확인한다(카카오 앱 시크릿 없이도 가능 — 사용자
 * 본인의 토큰으로 본인 정보만 조회하는 API라서). 확인되면 우리 DB에 사용자를 찾거나 새로
 * 만들고, 이후 API 호출에 쓸 우리 자체 JWT를 발급한다.
 */
@Service
public class KakaoAuthService {

    private final RestClient kakaoApiClient = RestClient.create("https://kapi.kakao.com");
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public KakaoAuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse loginWithKakaoAccessToken(String kakaoAccessToken) {
        KakaoUserInfo info = kakaoApiClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserInfo.class);

        User user = userRepository.findByKakaoId(info.id())
                .map(existing -> {
                    existing.updateProfile(info.nickname(), info.profileImageUrl());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        new User(info.id(), info.nickname(), info.email(), info.profileImageUrl())));

        String jwt = jwtService.issueToken(user.getId());
        return new AuthResponse(jwt, UserResponse.from(user));
    }
}
