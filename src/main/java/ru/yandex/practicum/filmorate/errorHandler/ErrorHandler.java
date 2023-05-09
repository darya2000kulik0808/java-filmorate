package ru.yandex.practicum.filmorate.errorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotInsertedException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAbsentObject(final ObjectNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse("Объект не найден", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleExistingObject(final ObjectAlreadyExistsException e) {
        return new ResponseEntity<>(
                new ErrorResponse("Объект уже существует.", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotInsertedObject(final NotInsertedException e) {
        return new ResponseEntity<>(
                new ErrorResponse("Не удалось добавить объект.", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(
                new ErrorResponse("Поля объекта заполнены неверно!", getErrorsMap(errors).toString()),
                HttpStatus.BAD_REQUEST
        );
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("Допущенные ошибки: ", errors);
        return errorResponse;
    }
}

