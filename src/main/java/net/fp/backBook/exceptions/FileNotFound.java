package net.fp.backBook.exceptions;

public class FileNotFound extends RuntimeException {
    public FileNotFound() {
    }

    public FileNotFound(String message) {
        super(message);
    }

    public FileNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFound(Throwable cause) {
        super(cause);
    }
}
