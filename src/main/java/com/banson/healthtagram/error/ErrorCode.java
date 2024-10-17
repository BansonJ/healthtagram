package com.banson.healthtagram.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BadRequest(800, "요청이 잘못됐어요"),
    NotAuthorized(801, "인증안됨;"),
    IllegalArgumentError(802, "값을 제대로 못넣으셨어요"),
    NullPointError(804, "값이 없네요");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
