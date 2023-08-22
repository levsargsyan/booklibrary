package com.example.booklibrary.book.service.impl;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryProjectedResponseDto;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.mapper.BookMapper;
import com.example.booklibrary.book.model.Book;
import com.example.booklibrary.book.model.Inventory;
import com.example.booklibrary.book.repository.BookRepository;
import com.example.booklibrary.book.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;

import static com.example.booklibrary.book.helper.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testGetBook_BookFound() {
        Long bookId = 1L;
        Book book = createBook();

        BookResponseDto responseDto = createBookResponseDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookResponseDto(book)).thenReturn(responseDto);

        BookResponseDto result = bookService.getBook(bookId);

        assertEquals(responseDto, result);
    }

    @Test
    void testGetBook_BookNotFound() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getBook(bookId)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetBookWithInventory_BookFound() {
        Long bookId = 1L;
        Book book = createBook();

        BookWithInventoryResponseDto responseDto = createWithInventoryResponseDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookWithInventoryResponseDto(book)).thenReturn(responseDto);

        BookWithInventoryResponseDto result = bookService.getBookWithInventory(bookId);

        assertEquals(responseDto, result);
    }

    @Test
    void testGetBookWithInventory_BookNotFound() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getBookWithInventory(bookId)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testSaveBook_IsbnNotInUse() {
        BookWithInventoryRequestDto requestDto = createWithInventoryRequestDto();

        Book book = createBook();
        BookWithInventoryResponseDto responseDto = createWithInventoryResponseDto();

        when(bookMapper.bookWithInventoryRequestDtoToBook(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.bookToBookWithInventoryResponseDto(book)).thenReturn(responseDto);

        BookWithInventoryResponseDto result = bookService.saveBook(requestDto);

        assertEquals(responseDto, result);
    }

    @Test
    void testGetAllBooksCount() {
        when(bookRepository.count()).thenReturn(5L);
        assertEquals(5L, bookService.getAllBooksCount());
    }

    @Test
    void testGetBooksPaginated() {
        Page<Book> mockPage = mock(Page.class);
        PageRequest pageable = PageRequest.of(0, 10);
        BookResponseDto responseDto = mock(BookResponseDto.class);

        Page<BookResponseDto> dtoPage = mock(Page.class);

        when(bookRepository.findAll(pageable)).thenReturn(mockPage);
        doReturn(dtoPage).when(mockPage).map(any(Function.class));
        when(dtoPage.getContent()).thenReturn(Collections.singletonList(responseDto));

        assertEquals(responseDto, bookService.getBooksPaginated(pageable).getContent().get(0));
    }

    @Test
    void testDecrementBookCount_EnoughInventory() {
        Long inventoryId = 1L;
        Inventory mockInventory = mock(Inventory.class);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(mockInventory));
        when(mockInventory.getCount()).thenReturn(11);

        bookService.decrementBookCount(inventoryId, 5);

        verify(mockInventory).setCount(6);
    }

    @Test
    void testDecrementBookCount_NotEnoughInventory() {
        Long inventoryId = 1L;
        Inventory mockInventory = mock(Inventory.class);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(mockInventory));
        when(mockInventory.getCount()).thenReturn(3);

        assertThrows(ResponseStatusException.class, () -> bookService.decrementBookCount(inventoryId, 5));
    }

    @Test
    void testUpdateBook() {
        Long id = 1L;
        BookWithInventoryRequestDto updatedDto = createWithInventoryRequestDto();
        BookWithInventoryResponseDto existingBookDto = createWithInventoryResponseDto();
        Book book = createBook();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.bookWithInventoryRequestDtoToBook(updatedDto)).thenReturn(book);
        when(bookMapper.bookToBookWithInventoryResponseDto(any(Book.class))).thenReturn(existingBookDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookWithInventoryResponseDto result = bookService.updateBook(id, updatedDto);

        assertNotNull(result);
    }

    @Test
    void testDeleteBook() {
        Long id = 1L;

        bookService.deleteBook(id);

        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    void testSearchBooks() {
        BookSearchCommand searchCommand = new BookSearchCommand();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = Page.empty();

        when(bookRepository.searchBooks(searchCommand, pageable)).thenReturn(bookPage);

        Page<BookResponseDto> result = bookService.searchBooks(searchCommand, pageable);

        assertNotNull(result);
    }

    @Test
    void testGetAvailableLastAddedBooks() {
        Integer count = 5;
        List<Book> books = List.of(createBook());
        BookWithInventoryResponseDto bookWithInventoryResponseDto = createWithInventoryResponseDto();

        when(bookRepository.findByOrderByIdDesc(any(PageRequest.class))).thenReturn(books);
        when(bookMapper.bookToBookWithInventoryResponseDto(any(Book.class))).thenReturn(bookWithInventoryResponseDto);

        List<BookWithInventoryResponseDto> result = bookService.getAvailableLastAddedBooks(count);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = List.of(createBook(), createBook());
        List<BookWithInventoryResponseDto> expectedDtos = List.of(
                createWithInventoryResponseDto(),
                createWithInventoryResponseDto()
        );

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.booksToBookWithInventoryResponseDtos(books)).thenReturn(expectedDtos);

        List<BookWithInventoryResponseDto> resultDtos = bookService.getAllBooks();

        assertEquals(expectedDtos, resultDtos);
        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).booksToBookWithInventoryResponseDtos(books);
    }

    @Test
    void testGetAvailableBooksByAuthors() {
        Set<String> authors = Set.of("Author1", "Author2");
        int count = 5;

        Book bookWithInventory1 = createBook();
        bookWithInventory1.setId(1L);
        bookWithInventory1.getInventory().setCount(2);
        Book bookWithInventory2 = createBook();
        bookWithInventory2.setId(2L);
        bookWithInventory2.getInventory().setCount(5);
        List<Book> mockBooks = List.of(bookWithInventory1, bookWithInventory2);

        BookWithInventoryResponseDto dtoWithInventory1 = createWithInventoryResponseDto();
        BookWithInventoryResponseDto dtoWithInventory2 = createWithInventoryResponseDto();
        dtoWithInventory1.getInventory().setCount(2);
        dtoWithInventory2.getInventory().setCount(5);

        List<BookWithInventoryResponseDto> expectedDtos = List.of(dtoWithInventory1, dtoWithInventory2);

        when(bookRepository.findByAuthorInOrderByPublishedDesc(authors, PageRequest.of(0, count))).thenReturn(mockBooks);
        when(bookMapper.bookToBookWithInventoryResponseDto(mockBooks.get(0))).thenReturn(dtoWithInventory1);
        when(bookMapper.bookToBookWithInventoryResponseDto(mockBooks.get(1))).thenReturn(dtoWithInventory2);

        List<BookWithInventoryResponseDto> resultDtos = bookService.getAvailableBooksByAuthors(authors, count);

        assertEquals(expectedDtos, resultDtos);

        verify(bookRepository, times(1)).findByAuthorInOrderByPublishedDesc(authors, PageRequest.of(0, count));
        verify(bookMapper, times(2)).bookToBookWithInventoryResponseDto(any(Book.class));
    }

    @Test
    void testGetAvailableBooksByGenres() {
        Set<String> genres = Set.of("Genre1", "Genre22");
        int count = 5;

        Book bookWithInventory1 = createBook();
        bookWithInventory1.setId(1L);
        bookWithInventory1.getInventory().setCount(2);
        Book bookWithInventory2 = createBook();
        bookWithInventory2.setId(2L);
        bookWithInventory2.getInventory().setCount(5);
        List<Book> mockBooks = List.of(bookWithInventory1, bookWithInventory2);

        BookWithInventoryResponseDto dtoWithInventory1 = createWithInventoryResponseDto();
        BookWithInventoryResponseDto dtoWithInventory2 = createWithInventoryResponseDto();
        dtoWithInventory1.getInventory().setCount(2);
        dtoWithInventory2.getInventory().setCount(5);

        List<BookWithInventoryResponseDto> expectedDtos = List.of(dtoWithInventory1, dtoWithInventory2);

        when(bookRepository.findByGenreInOrderByPublishedDesc(genres, PageRequest.of(0, count))).thenReturn(mockBooks);
        when(bookMapper.bookToBookWithInventoryResponseDto(mockBooks.get(0))).thenReturn(dtoWithInventory1);
        when(bookMapper.bookToBookWithInventoryResponseDto(mockBooks.get(1))).thenReturn(dtoWithInventory2);

        List<BookWithInventoryResponseDto> resultDtos = bookService.getAvailableBooksByGenres(genres, count);

        assertEquals(expectedDtos, resultDtos);

        verify(bookRepository, times(1)).findByGenreInOrderByPublishedDesc(genres, PageRequest.of(0, count));
        verify(bookMapper, times(2)).bookToBookWithInventoryResponseDto(any(Book.class));
    }

    @Test
    void testGetBookAuthor_BookFound() {
        Long bookId = 1L;
        Book book = createBook();
        String expectedAuthor = book.getAuthor();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        String author = bookService.getBookAuthor(bookId);

        assertEquals(expectedAuthor, author);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookAuthor_BookNotFound() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getBookAuthor(bookId)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("Book not found with ID: " + bookId, exception.getReason());
    }

    @Test
    void testGetBookGenre_BookFound() {
        Long bookId = 1L;
        Book book = createBook();
        String expectedGenre = book.getGenre();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        String genre = bookService.getBookGenre(bookId);

        assertEquals(expectedGenre, genre);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookGenre_BookNotFound() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getBookGenre(bookId)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("Book not found with ID: " + bookId, exception.getReason());
    }

    @Test
    void testGetBooksByAuthorAndGenre_BooksFound() {
        Set<String> authors = Set.of("Author1", "Author2");
        Set<String> genres = Set.of("Genre1", "Genre2");
        List<Book> mockBooks = Arrays.asList(createBook(), createBook());
        List<BookWithInventoryResponseDto> expectedDtos = Arrays.asList(
                createWithInventoryResponseDto(),
                createWithInventoryResponseDto()
        );

        when(bookRepository.findByAuthorInAndGenreIn(new ArrayList<>(authors), new ArrayList<>(genres)))
                .thenReturn(mockBooks);
        when(bookMapper.booksToBookWithInventoryResponseDtos(mockBooks)).thenReturn(expectedDtos);

        List<BookWithInventoryResponseDto> resultDtos = bookService.getBooksByAuthorAndGenre(authors, genres);

        assertEquals(expectedDtos.size(), resultDtos.size());
        assertTrue(expectedDtos.containsAll(resultDtos) && resultDtos.containsAll(expectedDtos));
        verify(bookRepository, times(1)).findByAuthorInAndGenreIn(new ArrayList<>(authors), new ArrayList<>(genres));
    }

    @Test
    void testGetBooksByAuthorAndGenre_NoBooksFound() {
        Set<String> authors = Set.of("Author1", "Author2");
        Set<String> genres = Set.of("Genre1", "Genre2");

        when(bookRepository.findByAuthorInAndGenreIn(new ArrayList<>(authors), new ArrayList<>(genres)))
                .thenReturn(Collections.emptyList());

        List<BookWithInventoryResponseDto> resultDtos = bookService.getBooksByAuthorAndGenre(authors, genres);

        assertTrue(resultDtos.isEmpty());
        verify(bookRepository, times(1)).findByAuthorInAndGenreIn(new ArrayList<>(authors), new ArrayList<>(genres));
    }

    @Test
    void testGetAllInventories_InventoriesFound() {
        List<InventoryProjectedResponseDto> mockInventories = List.of(
                createInventoryProjectedResponseDto(),
                createInventoryProjectedResponseDto()
        );

        when(inventoryRepository.findAllProjectedBy()).thenReturn(mockInventories);

        List<InventoryProjectedResponseDto> resultInventories = bookService.getAllInventories();

        assertEquals(mockInventories.size(), resultInventories.size());
        assertTrue(mockInventories.containsAll(resultInventories) && resultInventories.containsAll(mockInventories));
        verify(inventoryRepository, times(1)).findAllProjectedBy();
    }

    @Test
    void testGetAllInventories_NoInventoriesFound() {
        when(inventoryRepository.findAllProjectedBy()).thenReturn(Collections.emptyList());

        List<InventoryProjectedResponseDto> resultInventories = bookService.getAllInventories();

        assertTrue(resultInventories.isEmpty());
        verify(inventoryRepository, times(1)).findAllProjectedBy();
    }

    @Test
    void testAssemblePagedModel() {
        Pageable pageable = PageRequest.of(0, 5);
        String path = "/some-path";
        List<BookResponseDto> content = List.of(new BookResponseDto(), new BookResponseDto());
        Page<BookResponseDto> mockPage = new PageImpl<>(content, pageable, content.size());

        PagedModel<EntityModel<BookResponseDto>> result = bookService.assemblePagedModel(pageable, mockPage, path);

        assertEquals(mockPage.getSize(), result.getMetadata().getSize());
        assertEquals(mockPage.getNumber(), result.getMetadata().getNumber());
        assertEquals(mockPage.getTotalElements(), result.getMetadata().getTotalElements());
        assertEquals(mockPage.getTotalPages(), result.getMetadata().getTotalPages());
        assertEquals(mockPage.getContent().size(), result.getContent().size());
    }

    @Test
    void testCheckData_IsbnInUseAndExistingDtoNull() {
        BookWithInventoryRequestDto requestDto = new BookWithInventoryRequestDto();
        requestDto.setIsbn("12345");

        when(bookRepository.existsByIsbn(requestDto.getIsbn())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.checkData(requestDto, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Isbn already in use.", exception.getReason());
    }

    @Test
    void testCheckData_IsbnInUseAndExistingDtoDifferent() {
        BookWithInventoryRequestDto requestDto = new BookWithInventoryRequestDto();
        requestDto.setIsbn("12345");

        BookWithInventoryResponseDto existingDto = new BookWithInventoryResponseDto();
        existingDto.setIsbn("67890");

        when(bookRepository.existsByIsbn(requestDto.getIsbn())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookService.checkData(requestDto, existingDto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Isbn already in use.", exception.getReason());
    }

    @Test
    void testCheckData_IsbnNotInUse() {
        BookWithInventoryRequestDto requestDto = new BookWithInventoryRequestDto();
        requestDto.setIsbn("12345");

        BookWithInventoryResponseDto existingDto = new BookWithInventoryResponseDto();
        existingDto.setIsbn("12345");

        assertDoesNotThrow(() -> bookService.checkData(requestDto, existingDto));
    }


}
