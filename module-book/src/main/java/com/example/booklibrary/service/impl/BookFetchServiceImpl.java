package com.example.booklibrary.service.impl;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.service.BookFetchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@ConditionalOnProperty(name = "book.data.loader.enabled", havingValue = "true")
public class BookFetchServiceImpl implements BookFetchService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${book.data.loader.source.url.path}")
    private String path;

    @Value("${book.data.loader.source.url.param.quantity}")
    private Integer quantity;

    @Value("${book.data.loader.source.url.param.locale}")
    private String locale;

    public BookFetchServiceImpl(@Value("${book.data.loader.source.url.base}") String baseUrl, ObjectMapper objectMapper) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Book> fetchBooks() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("_quantity", quantity)
                        .queryParam("_locale", locale)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(response -> Flux.fromIterable(
                        objectMapper.convertValue(response.get("data"), new TypeReference<List<Book>>() {})
                ))
                .collectList()
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .block();
    }
}
