/*
package com.banson.healthtagram.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity notAuthorize(AuthorizationDeniedException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.NOTAUTHORIZED;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity badRequest(HttpClientErrorException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.BADREQUEST;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity illegalArgumentError(IllegalArgumentException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.ILLGALARGUMENT;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerException(NullPointerException e) {
        ErrorCode errorCode = ErrorCode.NULLPOINTERROR;
        log.error("Exception: {}", e);
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity authenticationException(AuthenticationException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.AUTHENTICATIONERROR;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity customException(CustomException e) {
        log.error("Exception: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity exception(Exception e) {
        log.error("Exception: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("{}", e.getStackTrace());
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }
}
*/
