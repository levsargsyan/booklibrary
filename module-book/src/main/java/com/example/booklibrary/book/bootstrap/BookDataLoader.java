package com.example.booklibrary.book.bootstrap;

import com.example.booklibrary.book.model.Book;
import com.example.booklibrary.book.model.Inventory;
import com.example.booklibrary.book.repository.BookRepository;
import com.example.booklibrary.book.service.BookFetchService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "book.data.loader.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class BookDataLoader implements ApplicationRunner {

    private final BookFetchService bookFetchService;
    private final BookRepository bookRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        try {
            if (bookRepository.count() == 0) {
                List<Book> books = bookFetchService.fetchBooks();

                books.forEach(book -> {
                    Inventory inventory = new Inventory();
                    inventory.setCount(10);
                    book.setInventory(inventory);
                    inventory.setBook(book);
                });

                bookRepository.saveAll(books);

                log.info("Data loaded from external service");
            } else {
                log.warn("Data already exists, therefore not loaded from external service");
            }
        } catch (ConstraintViolationException cve) {
            log.warn("Data already initialized by another instance.");
        } catch (Exception exception) {
            log.error("Exception thrown when trying to load book data from external service");
            throw exception;
        }
    }
}
