package com.bhattaditya2.book_service.service;

import com.bhattaditya2.book_service.payload.BookResponse;
import com.bhattaditya2.book_service.entity.Book;

import java.util.List;

public interface BookService {

    BookResponse fetchBookDetails(Long id);

    Book createBook(Book book);

    List<BookResponse> getAllBooks();

    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
