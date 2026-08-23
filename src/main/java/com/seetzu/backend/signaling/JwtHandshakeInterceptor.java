package com.seetzu.backend.signaling;

import com.seetzu.backend.auth.JwtService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * `/signal-user?token=<jwt>` 핸드셰이크 시 토큰을 검증해 인증된 사용자만 연결을 맺게 한다.
 * WebSocket 핸드셰이크는 HTTP 업그레이드 요청이라 커스텀 헤더를 모바일 클라이언트에서
 * 다루기보다 쿼리 파라미터로 받는 쪽이 더 간단해서 이 방식을 택함.
 */
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");
        if (token == null) {
            return false;
        }
        try {
            UUID userId = jwtService.parseUserId(token);
            attributes.put("userId", userId);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no-op
    }
}
