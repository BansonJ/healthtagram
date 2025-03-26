package com.banson.healthtagram.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BadRequest(5000, "요청이 잘못됐어요"),
    NotAuthorized(5003, "인증안됨;"),
    IllegalArgumentError(4003, "값을 제대로 못넣으셨어요"),
    NullPointError(5004, "값이 없네요"),
    AuthenticationError(5002, "인가가 안됐네요..");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
