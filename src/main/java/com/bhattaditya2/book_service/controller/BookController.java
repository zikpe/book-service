package com.bhattaditya2.book_service.controller;

import com.bhattaditya2.book_service.payload.BookResponse;
import com.bhattaditya2.book_service.entity.Book;
import com.bhattaditya2.book_service.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> fetchBookDetails(@PathVariable Long id) {
        BookResponse response = bookService.fetchBookDetails(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody Book book) {
        LOGGER.info("create book {}", book);
        BookResponse bookResponse= bookService.createBook(book);
        return new ResponseEntity<>(bookResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        LOGGER.info("fetch books");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody Book book) {
        BookResponse bookResponse= bookService.updateBook(id, book);
        return ResponseEntity.ok(bookResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully!");
    }
}
