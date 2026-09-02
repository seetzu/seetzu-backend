package com.seetzu.backend.push;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인한 유저의 기기별 FCM 토큰. 재설치 등으로 같은 물리 기기의 토큰이 다른 유저 것으로
 * 바뀔 수 있어 토큰 자체를 유니크 키로 두고, 등록 시 이미 있으면 유저를 재배정한다.
 */
@Entity
@Table(name = "device_tokens")
@Getter
@NoArgsConstructor
public class DeviceToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DevicePlatform platform;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DeviceToken(UUID userId, String token, DevicePlatform platform) {
        this.userId = userId;
        this.token = token;
        this.platform = platform;
        this.updatedAt = Instant.now();
    }

    public void reassign(UUID userId, DevicePlatform platform) {
        this.userId = userId;
        this.platform = platform;
        this.updatedAt = Instant.now();
    }
}
