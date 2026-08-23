package com.seetzu.backend.friend;

import com.seetzu.backend.auth.User;
import com.seetzu.backend.auth.UserRepository;
import com.seetzu.backend.auth.UserResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendService(
            UserRepository userRepository,
            FriendRequestRepository friendRequestRepository,
            FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public FriendRequestResponse sendRequest(UUID fromUserId, String friendCode) {
        User toUser = userRepository.findByFriendCode(friendCode.trim().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 친구 코드의 사용자를 찾을 수 없습니다."));

        if (toUser.getId().equals(fromUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }
        if (friendshipRepository.existsByUserIdAndFriendId(fromUserId, toUser.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 친구입니다.");
        }
        if (friendRequestRepository
                .findByFromUserIdAndToUserIdAndStatus(fromUserId, toUser.getId(), FriendRequestStatus.PENDING)
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 대기 중인 친구 요청이 있습니다.");
        }

        FriendRequest saved = friendRequestRepository.save(new FriendRequest(fromUserId, toUser.getId()));
        User fromUser = userRepository.findById(fromUserId).orElseThrow();
        return FriendRequestResponse.of(saved, fromUser);
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> listIncomingRequests(UUID userId) {
        return friendRequestRepository.findByToUserIdAndStatus(userId, FriendRequestStatus.PENDING).stream()
                .map(request -> FriendRequestResponse.of(
                        request, userRepository.findById(request.getFromUserId()).orElseThrow()))
                .toList();
    }

    @Transactional
    public void acceptRequest(UUID requestId, UUID actingUserId) {
        FriendRequest request = pendingRequestAddressedTo(requestId, actingUserId);
        request.accept();
        friendshipRepository.save(new Friendship(request.getFromUserId(), request.getToUserId()));
        friendshipRepository.save(new Friendship(request.getToUserId(), request.getFromUserId()));
    }

    @Transactional
    public void rejectRequest(UUID requestId, UUID actingUserId) {
        pendingRequestAddressedTo(requestId, actingUserId).reject();
    }

    private FriendRequest pendingRequestAddressedTo(UUID requestId, UUID toUserId) {
        FriendRequest request = friendRequestRepository.findByIdAndToUserId(requestId, toUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 처리된 친구 요청입니다.");
        }
        return request;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listFriends(UUID userId) {
        return friendshipRepository.findByUserId(userId).stream()
                .map(friendship -> UserResponse.from(userRepository.findById(friendship.getFriendId()).orElseThrow()))
                .toList();
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        if (!friendshipRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "친구가 아닙니다.");
        }
        friendshipRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendshipRepository.deleteByUserIdAndFriendId(friendId, userId);
    }
}
