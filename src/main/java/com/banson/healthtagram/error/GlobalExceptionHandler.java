package com.banson.healthtagram.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.http.auth.AuthenticationException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.crossstore.ChangeSetPersister;
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
        ErrorCode errorCode = ErrorCode.NotAuthorized;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode);
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity badRequest(HttpClientErrorException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.BadRequest;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity illegalArgumentError(IllegalArgumentException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.IllegalArgumentError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity nullPointerException(NullPointerException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.NullPointError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity authenticationException(AuthenticationException e) {
        log.error("Exception: {}", e);
        ErrorCode errorCode = ErrorCode.AuthenticationError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode);
    }

}
