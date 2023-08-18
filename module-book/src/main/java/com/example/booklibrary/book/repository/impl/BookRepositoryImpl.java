package com.example.booklibrary.book.repository.impl;

import com.example.booklibrary.book.constant.SearchFieldOperation;
import com.example.booklibrary.book.constant.SearchOperation;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.dto.search.TextSearchCriteria;
import com.example.booklibrary.book.model.Book;
import com.example.booklibrary.book.repository.BookRepositoryCustom;
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

        // text-based field predicates
        List<Predicate> textPredicates = new ArrayList<>();

        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("title"), searchCommand.getTitle());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("author"), searchCommand.getAuthor());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("genre"), searchCommand.getGenre());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("description"), searchCommand.getDescription());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("isbn"), searchCommand.getIsbn());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("image"), searchCommand.getImage());
        addTextPredicateIfNotNullOrEmpty(textPredicates, cb, root.get("publisher"), searchCommand.getPublisher());

        // text-based predicates using 'AND' or 'OR' based on user's choice
        if (!textPredicates.isEmpty()) {
            if (searchCommand.getOperation() == SearchOperation.OR) {
                predicates.add(cb.or(textPredicates.toArray(new Predicate[0])));
            } else {
                predicates.addAll(textPredicates);
            }
        }

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

    private void addTextPredicateIfNotNullOrEmpty(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, TextSearchCriteria criteria) {
        if (criteria != null && criteria.getValue() != null && !criteria.getValue().trim().isEmpty()) {
            if (criteria.getOperation() == SearchFieldOperation.EQUAL) {
                predicates.add(cb.equal(cb.lower(path), criteria.getValue().toLowerCase()));
            } else if (criteria.getOperation() == SearchFieldOperation.CONTAINS) {
                predicates.add(cb.like(cb.lower(path), "%" + criteria.getValue().toLowerCase() + "%"));
            }
        }
    }
}
