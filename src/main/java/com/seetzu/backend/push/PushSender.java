package com.seetzu.backend.push;

import java.util.Map;

/**
 * 실제 FCM/APNs 전송을 추상화. Firebase 자격증명이 준비되기 전엔 {@link LoggingPushSender}가
 * 대신 로그만 남기고, {@code push.enabled=true}가 되면 {@link FirebasePushSender}로 바뀐다.
 */
public interface PushSender {
    void send(String token, String title, String body, Map<String, String> data);
}
