package com.seetzu.backend.friend;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Optional<FriendRequest> findByIdAndToUserId(UUID id, UUID toUserId);

    List<FriendRequest> findByToUserIdAndStatus(UUID toUserId, FriendRequestStatus status);

    Optional<FriendRequest> findByFromUserIdAndToUserIdAndStatus(
            UUID fromUserId, UUID toUserId, FriendRequestStatus status);
}
