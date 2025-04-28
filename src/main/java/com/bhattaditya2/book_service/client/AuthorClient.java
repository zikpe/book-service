package com.bhattaditya2.book_service.client;

import com.bhattaditya2.book_service.entity.Author;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "author-service", url = "http://localhost:8081")
public interface AuthorClient {

    @GetMapping("/authors/{id}")
    Author getAuthorById(@PathVariable("id") Long id);
}
