package com.seetzu.backend.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, length = 50)
    private String nickname;

    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Instant createdAt = Instant.now();

    public User(Long kakaoId, String nickname, String email, String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
