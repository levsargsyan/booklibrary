package com.example.booklibrary.bootstrap;

import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.service.BookFetchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "book.data.loader.enabled", havingValue = "true")
@Slf4j
@AllArgsConstructor
public class BookDataLoader implements ApplicationRunner {

    private final BookFetchService bookFetchService;
    private final BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (bookRepository.findFirst().isEmpty()) {
                bookRepository.saveAll(bookFetchService.fetchBooks());
                log.info("Data loaded from external service");
            } else {
                log.warn("Data already exists; not loaded from external service");
            }
        } catch (Exception exception) {
            log.error("Exception thrown when trying to load book data from external service");
            throw exception;
        }
    }
}
