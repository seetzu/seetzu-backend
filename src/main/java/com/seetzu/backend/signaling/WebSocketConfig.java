package com.seetzu.backend.signaling;

import com.seetzu.backend.auth.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingWebSocketHandler signalingWebSocketHandler;
    private final UserSignalingWebSocketHandler userSignalingWebSocketHandler;
    private final JwtService jwtService;

    public WebSocketConfig(
            SignalingWebSocketHandler signalingWebSocketHandler,
            UserSignalingWebSocketHandler userSignalingWebSocketHandler,
            JwtService jwtService) {
        this.signalingWebSocketHandler = signalingWebSocketHandler;
        this.userSignalingWebSocketHandler = userSignalingWebSocketHandler;
        this.jwtService = jwtService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingWebSocketHandler, "/signal/*").setAllowedOrigins("*");
        registry.addHandler(userSignalingWebSocketHandler, "/signal-user")
                .addInterceptors(new JwtHandshakeInterceptor(jwtService))
                .setAllowedOrigins("*");
    }
}
