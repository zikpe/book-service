package com.bhattaditya2.book_service.service.impl;

import com.bhattaditya2.book_service.client.AuthorClient;
import com.bhattaditya2.book_service.dto.BookResponse;
import com.bhattaditya2.book_service.entity.Author;
import com.bhattaditya2.book_service.entity.Book;
import com.bhattaditya2.book_service.repository.BookRepository;
import com.bhattaditya2.book_service.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorClient authorClient;

    public BookServiceImpl(BookRepository bookRepository, AuthorClient authorClient) {
        this.bookRepository = bookRepository;
        this.authorClient = authorClient;
    }

    @Override
    public BookResponse fetchBookDetails(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("Book not found ID: " + id));

        Author author = authorClient.getAuthorById(book.getAuthorId());

        BookResponse response = new BookResponse();
        response.setBook(book);
        response.setAuthor(author);
        return response;
    }

    private Book fetchBook(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("Book not found ID: " + id));
    }

    @Override
    public Book createBook(Book book) {
        try {
            Author author = authorClient.getAuthorById(book.getAuthorId());
        } catch (Exception e) {
            throw new IllegalStateException("Author not found with ID: " + book.getAuthorId());
        }

        return bookRepository.save(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(book -> {
                    Author author = authorClient.getAuthorById(book.getAuthorId());
                    return new BookResponse(book, author);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book existingBook =fetchBook(id);
        Author author = authorClient.getAuthorById(book.getAuthorId());

        existingBook.setTitle(book.getTitle());
        existingBook.setGenre(book.getGenre());
        existingBook.setAuthorId(book.getAuthorId());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = fetchBook(id);

        bookRepository.delete(book);
    }
}
