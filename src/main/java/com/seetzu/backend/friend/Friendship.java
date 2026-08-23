package com.seetzu.backend.friend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 친구 요청이 수락되면 양방향으로 한 행씩(A→B, B→A) 만든다 — "내 친구 목록"을
 * {@code WHERE user_id = ?}만으로 조회할 수 있게 하기 위함.
 */
@Entity
@Table(name = "friends")
@Getter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "friend_id", nullable = false)
    private UUID friendId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Instant createdAt = Instant.now();

    public Friendship(UUID userId, UUID friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
