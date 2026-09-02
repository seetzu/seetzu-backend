package com.seetzu.backend.push;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping
    public void register(
            @AuthenticationPrincipal UUID userId, @Valid @RequestBody RegisterDeviceTokenRequest request) {
        deviceTokenService.register(userId, request.token(), request.platform());
    }

    public record RegisterDeviceTokenRequest(@NotBlank String token, @NotNull DevicePlatform platform) {}
}
