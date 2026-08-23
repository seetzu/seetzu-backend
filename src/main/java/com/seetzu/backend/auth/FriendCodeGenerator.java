package com.seetzu.backend.auth;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * 라인/디스코드 식 "친구 코드" 발급. 헷갈리기 쉬운 문자(0/O, 1/I/L)는 빼고,
 * DB의 unique 제약과 충돌하면 재시도한다(가입 시 1회만 호출되므로 충돌 확률은 무시할 수준).
 */
@Component
public class FriendCodeGenerator {

    private static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int LENGTH = 8;

    private final SecureRandom random = new SecureRandom();
    private final UserRepository userRepository;

    public FriendCodeGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUnique() {
        String code;
        do {
            code = generate();
        } while (userRepository.findByFriendCode(code).isPresent());
        return code;
    }

    private String generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
