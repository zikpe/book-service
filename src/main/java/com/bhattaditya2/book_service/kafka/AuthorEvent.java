package com.bhattaditya2.book_service.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorEvent {
    private Long id;
    private String name;
    private String email;
    private String dob;

    @Override
    public String toString() {
        return "AuthorEvent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}