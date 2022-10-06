package ru.practicum.shareit.exceptions;

public class NotFoundParameterException extends RuntimeException {
    public NotFoundParameterException(String message) {
        super(message);
    }
}
