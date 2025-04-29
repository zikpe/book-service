package com.bhattaditya2.book_service.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Author {
    private Long id;

    private String name;

    private String email;

    private LocalDate dob;

}
