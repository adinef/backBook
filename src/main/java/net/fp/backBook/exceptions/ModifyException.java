package net.fp.backBook.exceptions;

public class ModifyException extends RuntimeException {
    public ModifyException() {
    }

    public ModifyException(String message) {
        super(message);
    }

    public ModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModifyException(Throwable cause) {
        super(cause);
    }
}
