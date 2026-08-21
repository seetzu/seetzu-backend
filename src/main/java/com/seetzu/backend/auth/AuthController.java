package com.seetzu.backend.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    public AuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @PostMapping("/kakao")
    public AuthResponse loginWithKakao(@Valid @RequestBody KakaoLoginRequest request) {
        return kakaoAuthService.loginWithKakaoAccessToken(request.accessToken());
    }

    public record KakaoLoginRequest(@NotBlank String accessToken) {}
}
