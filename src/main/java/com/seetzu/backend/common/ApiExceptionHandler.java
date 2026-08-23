package com.seetzu.backend.common;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * {@link ResponseStatusException}의 reason을 HTTP 상태줄(sendError)이 아니라 JSON 바디로 응답한다.
 * Tomcat은 상태줄 reason phrase에 ISO-8859-1 밖 문자(한글 등)가 들어오면 응답을 정상 전송하지
 * 못하고 Spring Security의 기본 403으로 뭉개버리는 문제가 있어, 이를 피하기 위함이다.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", String.valueOf(ex.getReason())));
    }
}
