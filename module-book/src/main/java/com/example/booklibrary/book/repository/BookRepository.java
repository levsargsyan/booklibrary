package com.example.booklibrary.book.repository;

import com.example.booklibrary.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    boolean existsByIsbn(String isbn);
}
