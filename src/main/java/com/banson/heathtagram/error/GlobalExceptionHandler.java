package com.banson.heathtagram.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    ObjectMapper objectMapper = new ObjectMapper();

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

}
