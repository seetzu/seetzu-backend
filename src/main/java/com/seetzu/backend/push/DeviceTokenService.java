package com.seetzu.backend.push;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    @Transactional
    public void register(UUID userId, String token, DevicePlatform platform) {
        deviceTokenRepository.findByToken(token)
                .ifPresentOrElse(
                        existing -> existing.reassign(userId, platform),
                        () -> deviceTokenRepository.save(new DeviceToken(userId, token, platform)));
    }
}
