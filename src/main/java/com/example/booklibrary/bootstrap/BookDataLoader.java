package com.example.booklibrary.bootstrap;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.service.BookFetchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

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
            if (bookRepository.count() == 0) {
                List<Book> books = bookFetchService.fetchBooks();
                bookRepository.saveAll(books);

                log.info("Data is loaded from external service");
            } else {
                log.warn("Data already exists, therefore will not be loaded from external service");
            }
        } catch (Exception exception) {
            log.error("Exception thrown when trying to load book data from external service");
            throw exception;
        }
    }
}
