package com.bhattaditya2.book_service.service;

import com.bhattaditya2.book_service.dto.BookResponse;
import com.bhattaditya2.book_service.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BookService {

    BookResponse fetchBookDetails(Long id);

    Book createBook(Book book);

    List<BookResponse> getAllBooks();

    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
