package com.seetzu.backend.signaling;

import com.seetzu.backend.auth.JwtAuthenticationFilter;
import com.seetzu.backend.auth.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * `/auth/**`(카카오 로그인), `/signal/**`(방-코드 기반 WebRTC 시그널링, 익명), `/signal-user`
 * (로그인 유저 단위 시그널링 — 인증은 {@link JwtHandshakeInterceptor}가 핸드셰이크에서 별도로
 * 함)는 이 필터 체인 자체는 통과시키고, 나머지 API는 {@link JwtAuthenticationFilter}가 심어둔
 * 인증 정보를 요구한다.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handling -> handling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/signal/**", "/signal-user").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
