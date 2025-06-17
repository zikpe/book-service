package com.bhattaditya2.book_service.payload;

import com.bhattaditya2.book_service.entity.Author;
import com.bhattaditya2.book_service.entity.Book;
import lombok.Getter;

@Getter
public class BookResponse {

    private Book book;

    private Author author;  // 🌟 Author fetched from author-service

    public BookResponse() {
    }

    public BookResponse(Book book, Author author) {
        this.book = book;
        this.author = author;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}