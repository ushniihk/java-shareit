package ru.practicum.shareit.exceptions;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

class ErrorHandlerTest {
    ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleIncorrectParameterException() {
        ErrorHandler errorHandler = new ErrorHandler();
        IncorrectParameterException exception = new IncorrectParameterException("bad");
        errorHandler.handleIncorrectParameterException(exception);
        MatcherAssert.assertThat(exception.getMessage(),
                equalTo(errorHandler.handleIncorrectParameterException(exception).getError()));
    }

    @Test
    void handleCreatingException() {
        ErrorHandler errorHandler = new ErrorHandler();
        CreatingException exception = new CreatingException("bad");
        errorHandler.handleCreatingException(exception);
        MatcherAssert.assertThat(exception.getMessage(),
                equalTo(errorHandler.handleCreatingException(exception).getError()));
    }

    @Test
    void handleNotFoundParameterException() {
        ErrorHandler errorHandler = new ErrorHandler();
        NotFoundParameterException exception = new NotFoundParameterException("bad");
        errorHandler.handleNotFoundParameterException(exception);
        MatcherAssert.assertThat(exception.getMessage(),
                equalTo(errorHandler.handleNotFoundParameterException(exception).getError()));
    }

    @Test
    void handleUpdateException() {
        ErrorHandler errorHandler = new ErrorHandler();
        UpdateException exception = new UpdateException("bad");
        errorHandler.handleUpdateException(exception);
        MatcherAssert.assertThat(exception.getMessage(),
                equalTo(errorHandler.handleUpdateException(exception).getError()));
    }

    @Test
    void handleThrowableException() {
        ErrorHandler errorHandler = new ErrorHandler();
        Throwable exception = new Throwable("bad");
        errorHandler.handleThrowableException(exception);
        MatcherAssert.assertThat(exception.getMessage(),
                equalTo(errorHandler.handleThrowableException(exception).getError()));
    }
}