package com.explorewithme.server.controller;

import com.explorewithme.server.dto.ErrorMessageDto;
import com.explorewithme.server.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class CommonExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({CategoryNotFoundException.class, CompilationNotFoundException.class,
            EventNotFoundException.class, UserNotFoundException.class, RequestNotFoundException.class})
    public ResponseEntity<ErrorMessageDto> handleNotFoundException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorMessageDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage(), ex);
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessageDto);
    }

    @ExceptionHandler({ValidationException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorMessageDto> handleValidationException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Validation error.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessageDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageDto> onConstraintValidationException(ConstraintViolationException ex) {
        logger.error(ex.getMessage(), ex);
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("ConstraintViolation error.")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorMessageDto);
    }

    @ExceptionHandler({RequestValidationException.class, EventValidationException.class})
    public ResponseEntity<ErrorMessageDto> onValicationException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Validation error.")
                .message(ex.getMessage())
                .trace(ex.getStackTrace())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorMessageDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> onException(Throwable ex) {
        logger.error(ex.getMessage(), ex);
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(ex.getMessage())
                .trace(ex.getStackTrace())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorMessageDto);
    }

}
