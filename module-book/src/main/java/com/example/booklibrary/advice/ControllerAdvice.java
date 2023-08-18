package com.example.booklibrary.advice;

import com.example.booklibrary.controller.BookController;
import com.example.booklibrary.controller.BookPagedController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Slf4j
@RestControllerAdvice(assignableTypes = {BookController.class, BookPagedController.class})
public class ControllerAdvice {

    public static final String EXCEPTION_MSG = "Exception is thrown";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error(EXCEPTION_MSG, ex);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                errors);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public Map<String, Object> handleNotFoundException(NoSuchElementException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                "Resource not found.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGeneralExceptions(Exception ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Error",
                "An unexpected error occurred. Please try again later.");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                "Request could not be completed due to a data conflict.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method Not Allowed",
                List.of(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request. Please check the request body.",
                List.of(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public Map<String, Object> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        log.error(EXCEPTION_MSG, ex);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid data access usage",
                ex.getMessage());
    }


    private Map<String, Object> buildErrorResponse(HttpStatus status, String error, Object message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", new Date());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
