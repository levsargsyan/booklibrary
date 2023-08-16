package com.example.booklibrary.repository.impl;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepositoryCustom;
import com.example.booklibrary.search.BookSearchCommand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Book> searchBooks(BookSearchCommand searchCommand, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Book> dataQuery = cb.createQuery(Book.class);
        Root<Book> bookRoot = dataQuery.from(Book.class);

        List<Predicate> dataPredicates = createPredicates(searchCommand, cb, bookRoot);
        dataQuery.where(dataPredicates.toArray(new Predicate[0]));
        dataQuery.select(bookRoot);

        TypedQuery<Book> typedDataQuery = entityManager.createQuery(dataQuery);
        typedDataQuery.setFirstResult((int) pageable.getOffset());
        typedDataQuery.setMaxResults(pageable.getPageSize());

        List<Book> bookList = typedDataQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> countRoot = countQuery.from(Book.class);
        List<Predicate> countPredicates = createPredicates(searchCommand, cb, countRoot);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(bookList, pageable, count);
    }

    private List<Predicate> createPredicates(BookSearchCommand searchCommand, CriteriaBuilder cb, Root<Book> root) {
        List<Predicate> predicates = new ArrayList<>();

        addIfNotNullOrEmpty(predicates, cb, root.get("title"), searchCommand.getTitle());
        addIfNotNullOrEmpty(predicates, cb, root.get("author"), searchCommand.getAuthor());
        addIfNotNullOrEmpty(predicates, cb, root.get("genre"), searchCommand.getGenre());
        addIfNotNullOrEmpty(predicates, cb, root.get("description"), searchCommand.getDescription());
        addIfNotNullOrEmpty(predicates, cb, root.get("isbn"), searchCommand.getIsbn());
        addIfNotNullOrEmpty(predicates, cb, root.get("image"), searchCommand.getImage());
        addIfNotNullOrEmpty(predicates, cb, root.get("publisher"), searchCommand.getPublisher());

        String fieldPublished = "published";
        if (searchCommand.getPublished() != null) {
            if (searchCommand.getPublishedFrom() != null || searchCommand.getPublishedTo() != null) {
                throw new IllegalArgumentException("Please provide either a specific published date or a range, not both.");
            }
            predicates.add(cb.equal(root.get(fieldPublished), searchCommand.getPublished()));
        } else {
            if (searchCommand.getPublishedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(fieldPublished), searchCommand.getPublishedFrom()));
            }
            if (searchCommand.getPublishedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(fieldPublished), searchCommand.getPublishedTo()));
            }
        }

        return predicates;
    }

    private void addIfNotNullOrEmpty(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (value != null && !value.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }
}
