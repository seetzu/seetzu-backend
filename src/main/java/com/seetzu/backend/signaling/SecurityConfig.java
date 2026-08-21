package com.seetzu.backend.signaling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * /auth/kakao로 카카오 로그인 후 JWT를 발급하긴 하지만, 아직 그 JWT로 다른 API를
 * 보호하는 필터는 없다(친구 API를 붙일 때 추가 예정) — 지금은 전부 permitAll.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
