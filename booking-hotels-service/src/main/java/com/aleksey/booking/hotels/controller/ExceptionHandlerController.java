package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.response.ErrorResponse;
import com.aleksey.booking.hotels.exception.AlreadyExistsException;
import com.aleksey.booking.hotels.exception.RoomsUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> notValid(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        String errorMessage = String.join("; ", errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(RoomsUnavailableException.class)
    public ResponseEntity<ErrorResponse> roomsUnavailable(RoomsUnavailableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> alreadyExists(AlreadyExistsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus httpStatus, Exception ex) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.builder()
                        .errorMessage(ex.getMessage())
                        .build());
    }
}