package com.seetzu.backend.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * {@code push.enabled=true}이고 서비스계정 자격증명 파일이 준비됐을 때만 활성화되는 실제
 * FCM 발송자. Firebase 프로젝트가 없는 동안은 이 빈이 아예 만들어지지 않고
 * {@link LoggingPushSender}가 대신 쓰인다.
 */
@Component
@ConditionalOnProperty(prefix = "push", name = "enabled", havingValue = "true")
public class FirebasePushSender implements PushSender {

    private static final Logger log = LoggerFactory.getLogger(FirebasePushSender.class);

    private final FirebaseMessaging messaging;

    public FirebasePushSender(@Value("${firebase.credentials-path}") String credentialsPath) throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                .build();
        FirebaseApp app =
                FirebaseApp.getApps().isEmpty() ? FirebaseApp.initializeApp(options) : FirebaseApp.getInstance();
        this.messaging = FirebaseMessaging.getInstance(app);
    }

    @Override
    public void send(String token, String title, String body, Map<String, String> data) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(data)
                .build();
        try {
            messaging.send(message);
        } catch (Exception e) {
            log.warn("[push] failed to send to {}: {}", token, e.getMessage());
        }
    }
}
