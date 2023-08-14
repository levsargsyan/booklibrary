package com.example.booklibrary.service;

import com.example.booklibrary.dto.BookDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookResponseDto> getAllBooks() {
        return bookMapper.booksToBookResponseDtos(bookRepository.findAll());
    }

    public BookResponseDto getBookResponseById(Long id) {
        Book book = bookRepository.findById(id).orElse(null); // or throw an exception if not found
        return bookMapper.bookToBookResponseDto(book);
    }

    public BookDto getBookDtoById(Long id) {
        Book book = bookRepository.findById(id).orElse(null); // or throw an exception if not found
        return bookMapper.bookToBookDto(book);
    }

    public BookDto saveBook(BookDto bookDto) {
        Book book = bookRepository.save(bookMapper.bookDtoToBook(bookDto));
        return bookMapper.bookToBookDto(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public PagedModel<EntityModel<BookResponseDto>> assemblePagedModel(Page<BookResponseDto> booksPaginatedDto) {
        List<EntityModel<BookResponseDto>> bookDtoResources = booksPaginatedDto.getContent()
                .stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                booksPaginatedDto.getSize(),
                booksPaginatedDto.getNumber(),
                booksPaginatedDto.getTotalElements(),
                booksPaginatedDto.getTotalPages());

        return PagedModel.of(bookDtoResources, metadata);
    }

    public Page<BookResponseDto> getBooksPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::bookToBookResponseDto);
    }
}

