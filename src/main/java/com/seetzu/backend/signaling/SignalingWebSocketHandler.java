package com.seetzu.backend.signaling;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 1단계 기술 스파이크용 시그널링 릴레이.
 * 로그인 없이 방 코드(roomCode)만으로 두 기기를 묶어 SDP/ICE 메시지를 그대로 상대에게 전달한다.
 * 인증/영속화는 2단계(친구 기능)에서 다시 설계한다.
 */
@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        rooms.computeIfAbsent(roomCodeOf(session), key -> new CopyOnWriteArraySet<>()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomCode = roomCodeOf(session);
        Set<WebSocketSession> peers = rooms.getOrDefault(roomCode, Set.of());
        for (WebSocketSession peer : peers) {
            if (peer.isOpen() && !peer.getId().equals(session.getId())) {
                peer.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Set<WebSocketSession> peers = rooms.get(roomCodeOf(session));
        if (peers != null) {
            peers.remove(session);
            if (peers.isEmpty()) {
                rooms.remove(roomCodeOf(session));
            }
        }
    }

    private String roomCodeOf(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
