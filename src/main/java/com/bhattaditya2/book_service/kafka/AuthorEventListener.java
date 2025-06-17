package com.bhattaditya2.book_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthorEventListener {

    @KafkaListener(
            topics = "author-topic",
            groupId = "book-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAuthorEvent(AuthorEvent event) {
        System.out.println("📘 Received author event in book-service: " + event);
        // Process logic here (e.g., update book references, create author relation, etc.)
    }
}