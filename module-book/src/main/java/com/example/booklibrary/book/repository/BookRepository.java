package com.example.booklibrary.book.repository;

import com.example.booklibrary.book.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    boolean existsByIsbn(String isbn);

    List<Book> findByOrderByIdDesc(Pageable pageable);

    List<Book> findByAuthorInOrderByPublishedDesc(Set<String> authors, Pageable pageable);

    List<Book> findByGenreInOrderByPublishedDesc(Set<String> genres, Pageable pageable);

    List<Book> findByAuthorInAndGenreIn(List<String> authors, List<String> genres);
}
