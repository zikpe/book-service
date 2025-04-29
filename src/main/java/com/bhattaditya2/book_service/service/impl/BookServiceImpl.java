package com.bhattaditya2.book_service.service.impl;

import com.bhattaditya2.book_service.client.AuthorClient;
import com.bhattaditya2.book_service.exception.AuthorNotFoundException;
import com.bhattaditya2.book_service.exception.BookServiceException;
import com.bhattaditya2.book_service.payload.BookResponse;
import com.bhattaditya2.book_service.entity.Author;
import com.bhattaditya2.book_service.entity.Book;
import com.bhattaditya2.book_service.exception.BookNotFoundException;
import com.bhattaditya2.book_service.repository.BookRepository;
import com.bhattaditya2.book_service.service.BookService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final AuthorClient authorClient;

    public BookServiceImpl(BookRepository bookRepository, AuthorClient authorClient) {
        this.bookRepository = bookRepository;
        this.authorClient = authorClient;
    }

    @Override
    public BookResponse fetchBookDetails(Long id) {
        Book book = fetchBook(id);

        Author author;

        try {
            author = authorClient.getAuthorById(book.getAuthorId());
        } catch (FeignException e) {
            logger.error("Author service not reachable or author not found for authorId: {}. Using fallback dummy author.", book.getAuthorId(), e);

            // Fallback
           author = buildDummyAuthor();
        }

        return new BookResponse(book, author);
    }

    private Book fetchBook(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book not found ID: " + id));
    }

    private Author buildDummyAuthor() {
        Author author = new Author();
        author.setId(0L);
        author.setName("Unknown Author");
        author.setEmail("Author information temporarily unavailable");
        author.setDob(null);
        return author;
    }

    @Override
    public Book createBook(Book book) {
        try {
            Author author = authorClient.getAuthorById(book.getAuthorId());
        } catch (FeignException.NotFound ex) {
            logger.error("Author not found for authorId: {}", book.getAuthorId(), ex);
            throw new AuthorNotFoundException("Cannot create book because Author not found with ID: " + book.getAuthorId());
        } catch (FeignException ex) {
            logger.error("Author service not reachable while creating book for authorId: {}", book.getAuthorId(), ex);
            throw new RuntimeException("Author service unavailable. Please try again later.");
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
        Book existingBook = fetchBook(id);

        try {
            Author author = authorClient.getAuthorById(book.getAuthorId());
        } catch (FeignException.NotFound ex) {
            logger.error("Author not found with ID: {} while updating book.", book.getAuthorId(), ex);
            throw new AuthorNotFoundException("Author not found with ID: " + book.getAuthorId());
        } catch (Exception e) {
            logger.error("Error while updating book with ID: {}. Possible author-service issue.", id, e);
            throw new BookServiceException("Error communicating with author-service while updating book", e);
        }

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
