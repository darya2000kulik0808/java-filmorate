package ru.yandex.practicum.filmorate.exception;

public class NotInsertedException extends RuntimeException{
    public NotInsertedException(String message){
        super(message);
    }
}
