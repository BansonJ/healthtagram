package com.banson.healthtagram.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BADREQUEST(444, "요청이 잘못됐어요"),
    NOTAUTHORIZED(533, "인증안됨;"),
    ILLGALARGUMENT(445, "값을 제대로 못넣으셨어요"),
    NULLPOINTERROR(544, "값이 없네요"),
    AUTHENTICATIONERROR(534, "인가가 안됐네요.."),
    RUNTIMEEXCEPTION(500, "서버에서 문제가 발생!"),
    INTERNAL_SERVER_ERROR(999, "예상치 못한 에러가 발생!");


    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
