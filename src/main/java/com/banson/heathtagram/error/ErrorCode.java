package com.banson.heathtagram.error;

import lombok.Builder;
import lombok.Getter;

@Getter
public enum ErrorCode {
    BadRequest(800, "무슨 요청을 하신겁니까?"),
    NotAuthorized(801, "인증안됨;"),
    IllegalArgumentError(802, "값을 제대로 못넣으셨어요");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
