/*
package com.banson.healthtagram.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity notAuthorize() {
        ErrorCode errorCode = ErrorCode.NotAuthorized;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity badRequest() {
        ErrorCode errorCode = ErrorCode.BadRequest;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,MethodArgumentNotValidException.class})
    public ResponseEntity illegalArgumentError() throws JsonProcessingException {
        ErrorCode errorCode = ErrorCode.IllegalArgumentError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity internalException() {
        ErrorCode errorCode = ErrorCode.InternalError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity nullPointerException() {
        ErrorCode errorCode = ErrorCode.NullPointError;
        return ResponseEntity.status(errorCode.getCode()).body(errorCode.getMessage());
    }

}
*/
