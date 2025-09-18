package com.simplesdental.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

    @RestControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handle(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handle(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handle(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handle(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(ex.getMessage());
    }
}
