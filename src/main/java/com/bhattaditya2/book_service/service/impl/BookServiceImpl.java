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

import java.util.ArrayList;
import java.util.List;

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

        Author author = fetchAuthor(book.getAuthorId());

        return new BookResponse(book, author);
    }

    public Author fetchAuthor(Long id) {
        Author author;

        try {
            author = authorClient.getAuthorById(id);
        } catch (FeignException.NotFound ex) {
            // Fallback
            author = buildDummyAuthor("Author Not found with ID: " + id);
        } catch (FeignException e) {
            logger.error("Author service not reachable", e);
            // Fallback
            author = buildDummyAuthor("Author information temporarily unavailable");
        }
        return author;
    }

    private Book fetchBook(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book not found ID: " + id));
    }

    private Author buildDummyAuthor(String msg) {
        Author author = new Author();
        author.setId(null);
        author.setName("Unknown Author");
        author.setEmail(msg);
        author.setDob(null);
        return author;
    }

    @Override
    public BookResponse createBook(Book book) {
        Author author = fetchAuthor(book.getAuthorId());

        Book newBook = bookRepository.save(book);
        return fetchBookDetails(newBook.getId());

    }

    @Override
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        List<BookResponse> bookResponses = new ArrayList<>();

        books.forEach(book -> {
            BookResponse bookResponse = fetchBookDetails(book.getId());
            bookResponses.add(bookResponse);
        });
        return bookResponses;
    }

    @Override
    public BookResponse updateBook(Long id, Book book) {
        if (book.getAuthorId() == null) {
            logger.error("Author ID is null while updating book with ID: {}", id);
            throw new BookServiceException("Author ID cannot be null while updating book.");
        }
        Book existingBook = fetchBook(id);
        logger.info("Calling author-service for authorId: {}", existingBook.getAuthorId());

        Author author;
        try {
            author = authorClient.getAuthorById(book.getAuthorId());
            logger.info("Updating book with author: {}", author.getName());
        } catch (FeignException.NotFound ex) {
            logger.error("Author not found with ID: {} while updating book.", book.getAuthorId(), ex);
            throw new AuthorNotFoundException("Author not found with ID: " + book.getAuthorId());
        } catch (Exception e) {
            logger.error("Error while updating book with ID: {}. Possible author-service issue.", id, e);
            throw new BookServiceException("Error communicating with author-service while updating book", e);
        }

        existingBook.setAuthorId(book.getAuthorId());

        if (book.getTitle() != null) {
            existingBook.setTitle(book.getTitle());
        }

        if (book.getGenre() != null) {
            existingBook.setGenre(book.getGenre());
        }

        Book updatedBook = bookRepository.save(existingBook);
        return new BookResponse(existingBook, author);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = fetchBook(id);

        bookRepository.delete(book);
    }
}
