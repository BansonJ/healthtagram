/*
package com.banson.healthtagram.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity notAuthorize(AuthorizationDeniedException e) {
        ErrorCode errorCode = ErrorCode.NotAuthorized;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity badRequest(BadRequestException e) {
        ErrorCode errorCode = ErrorCode.BadRequest;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentError(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.IllegalArgumentError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerException(NullPointerException e) {
        ErrorCode errorCode = ErrorCode.NullPointError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

}
*/
