package com.seetzu.backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code Authorization: Bearer <jwt>} 헤더가 있고 유효하면 인증된 사용자(UUID)를
 * SecurityContext에 심는다. 헤더가 없거나 토큰이 유효하지 않으면 그냥 통과시키고,
 * 이후 인가 단계(SecurityConfig)에서 익명 요청으로 처리되게 둔다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                UUID userId = jwtService.parseUserId(header.substring("Bearer ".length()));
                var authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ignored) {
                // 토큰이 없거나 위조/만료됨 — 인증 없이 통과, 보호된 엔드포인트는 401로 막힘.
            }
        }
        filterChain.doFilter(request, response);
    }
}
