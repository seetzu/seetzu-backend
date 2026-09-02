package com.seetzu.backend.signaling;

import com.seetzu.backend.auth.User;
import com.seetzu.backend.auth.UserRepository;
import com.seetzu.backend.friend.FriendshipRepository;
import com.seetzu.backend.push.PushNotificationService;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 로그인한 유저 단위 시그널링 채널(`/signal-user`). 방-코드 없이 친구의 userId로 곧장
 * "공유 요청 → 수락/거절 → SDP/ICE 릴레이"를 라우팅한다. 유저당 세션 하나만 유지하며
 * (같은 유저가 다시 붙으면 이전 연결은 닫는다), 상대가 오프라인이면 요청자에게 바로 실패를
 * 알리는 동시에 {@link PushNotificationService}로 오프라인인 상대에게 푸시를 보낸다
 * (Firebase 자격증명이 없으면 {@code LoggingPushSender}가 로그만 남긴다).
 */
@Component
public class UserSignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(UserSignalingWebSocketHandler.class);

    private final ConcurrentHashMap<UUID, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PushNotificationService pushNotificationService;

    public UserSignalingWebSocketHandler(
            UserRepository userRepository,
            FriendshipRepository friendshipRepository,
            PushNotificationService pushNotificationService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        UUID userId = userIdOf(session);
        WebSocketSession previous = onlineUsers.put(userId, session);
        if (previous != null && previous.isOpen() && !previous.getId().equals(session.getId())) {
            previous.close(CloseStatus.NORMAL);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        UUID userId = userIdOf(session);
        ObjectNode node = (ObjectNode) objectMapper.readTree(message.getPayload());
        String type = node.path("type").asText();
        UUID to;
        try {
            to = UUID.fromString(node.path("to").asText());
        } catch (IllegalArgumentException e) {
            return;
        }

        if ("share-request".equals(type)) {
            if (!friendshipRepository.existsByUserIdAndFriendId(userId, to)) {
                sendTo(userId, failure("not-friends"));
                return;
            }
            User fromUser = userRepository.findById(userId).orElseThrow();
            WebSocketSession target = onlineUsers.get(to);
            if (target == null || !target.isOpen()) {
                sendTo(userId, failure("offline"));
                userRepository.findById(to)
                        .ifPresent(toUser -> pushNotificationService.notifyShareRequest(toUser, fromUser));
                return;
            }
            node.set("from", profileNode(fromUser));
            target.sendMessage(new TextMessage(objectMapper.writeValueAsString(node)));
            return;
        }

        node.put("from", userId.toString());
        WebSocketSession target = onlineUsers.get(to);
        if (target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(objectMapper.writeValueAsString(node)));
        } else {
            log.warn("dropped {} message from={} to={} — target session missing/closed", type, userId, to);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID userId = userIdOf(session);
        onlineUsers.remove(userId, session);
    }

    private UUID userIdOf(WebSocketSession session) {
        return (UUID) session.getAttributes().get("userId");
    }

    private void sendTo(UUID userId, Map<String, String> payload) throws IOException {
        WebSocketSession session = onlineUsers.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        }
    }

    private Map<String, String> failure(String reason) {
        return Map.of("type", "share-request-failed", "reason", reason);
    }

    private ObjectNode profileNode(User user) {
        ObjectNode profile = objectMapper.createObjectNode();
        profile.put("id", user.getId().toString());
        profile.put("nickname", user.getNickname());
        if (user.getProfileImageUrl() != null) {
            profile.put("profileImageUrl", user.getProfileImageUrl());
        }
        return profile;
    }
}
