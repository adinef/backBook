package net.fp.backBook.exceptions;

public class AddException extends RuntimeException {
    public AddException() {
    }

    public AddException(String message) {
        super(message);
    }

    public AddException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddException(Throwable cause) {
        super(cause);
    }
}
