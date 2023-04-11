package ru.yandex.practicum.filmorate.errorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

@ControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAbsentObject(final ObjectNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse("Объект не найден", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
