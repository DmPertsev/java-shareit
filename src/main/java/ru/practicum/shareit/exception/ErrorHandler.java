package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.error("Validation error! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        log.error("Validation error! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException exception) {
        log.error("Object not found! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse duplicatedExceptionHandler(DuplicatedEmailException exception) {
        log.error("Duplicate email! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidException(final ValidationException exception) {
        log.error("Validation error! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableException(final UnavailableException exception) {
        log.error("Item is not available! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.error("Invalid argument! {}", exception.getMessage());
        return new ErrorResponse("Unknown state: " + exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final Exception exception) {
        log.error("Server error! {}", exception.getMessage());
        return new ErrorResponse(
                exception.getMessage()
        );
    }
}