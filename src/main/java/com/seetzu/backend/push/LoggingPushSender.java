package com.seetzu.backend.push;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/** Firebase가 설정되기 전까지의 기본 발송자 — 실제로 보내지 않고 로그만 남긴다. */
@Component
@ConditionalOnMissingBean(FirebasePushSender.class)
public class LoggingPushSender implements PushSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingPushSender.class);

    @Override
    public void send(String token, String title, String body, Map<String, String> data) {
        log.info("[push] would send to {}: {} / {} / {}", token, title, body, data);
    }
}
