package com.seetzu.backend.friend;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    List<Friendship> findByUserId(UUID userId);

    boolean existsByUserIdAndFriendId(UUID userId, UUID friendId);

    void deleteByUserIdAndFriendId(UUID userId, UUID friendId);
}
