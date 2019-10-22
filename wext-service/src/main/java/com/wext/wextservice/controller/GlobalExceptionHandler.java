package com.wext.wextservice.controller;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.AuthorityLimitException;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.domain.exception.UserTargetException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e) {
        return ResponseEntity.status(404).body(
                BaseResponse.failResponse(e.getMessage())
        );
    }

    @ExceptionHandler
    public ResponseEntity authorityLimitHandler (AuthorityLimitException e) {
        return ResponseEntity.status(403).body(
                BaseResponse.failResponse(e.getMessage())
        );
    }


    @ExceptionHandler
    public ResponseEntity invalidOperationHandler(InvalidOperationException e) {
        return ResponseEntity.status(400).body(
                BaseResponse.failResponse(e.getMessage())
        );
    }

    @ExceptionHandler
    public ResponseEntity nonExistentException(NonExistentException e) {
        return ResponseEntity.status(404).body(
                BaseResponse.failResponse(e.getMessage())
        );
    }

    @ExceptionHandler
    public ResponseEntity userTargetHandler(UserTargetException e) {
        return ResponseEntity.status(404).body(
                BaseResponse.failResponse(e.getMessage())
        );
    }

}
