package com.example.gitscorer.exception;

import feign.FeignException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalFeignExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public void handleFeignException(FeignException exception) {
        switch(exception.status()) {
            case 503 -> throw new GithubServiceUnavailableException("Service is unavailable due to too many exceptions!");
        }
    }
}
