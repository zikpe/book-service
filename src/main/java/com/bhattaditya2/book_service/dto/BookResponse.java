package com.bhattaditya2.book_service.dto;

import com.bhattaditya2.book_service.entity.Author;
import com.bhattaditya2.book_service.entity.Book;

public class BookResponse {

    private Book book;

    private Author author;  // 🌟 Author fetched from author-service

    public BookResponse() {
    }

    public BookResponse(Book book, Author author) {
        this.book = book;
        this.author = author;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}