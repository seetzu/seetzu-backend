package com.seetzu.backend.push;

import com.seetzu.backend.auth.User;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final PushSender pushSender;

    public PushNotificationService(DeviceTokenRepository deviceTokenRepository, PushSender pushSender) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.pushSender = pushSender;
    }

    @Transactional(readOnly = true)
    public void notifyShareRequest(User recipient, User requester) {
        String title = requester.getNickname() + "님이 화면을 보여주려 해요";
        String body = "지금 확인해보세요";
        Map<String, String> data = Map.of(
                "type", "share-request",
                "fromUserId", requester.getId().toString(),
                "fromNickname", requester.getNickname());
        for (DeviceToken deviceToken : deviceTokenRepository.findByUserId(recipient.getId())) {
            pushSender.send(deviceToken.getToken(), title, body, data);
        }
    }
}
