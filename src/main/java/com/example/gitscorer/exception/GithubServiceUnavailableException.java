package com.example.gitscorer.exception;

public class GithubServiceUnavailableException extends RuntimeException {
    public GithubServiceUnavailableException(String message){
        super(message);
    }
}
