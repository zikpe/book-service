package com.bhattaditya2.book_service.repository;

import com.bhattaditya2.book_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
