package com.bhattaditya2.book_service.exception;

public class BookServiceException extends RuntimeException{
    public BookServiceException(String message, Exception e) {
        super(message);
    }

    public BookServiceException(String message) {
    }
}
