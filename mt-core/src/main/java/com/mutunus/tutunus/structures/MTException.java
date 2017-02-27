package com.mutunus.tutunus.structures;

public class MTException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MTException() {
        super();
    }

    public MTException(String message) {
        super(message);
    }

    public MTException(Throwable cause) {
        super(cause);
    }

    public MTException(String message, Throwable cause) {
        super(message, cause);
    }

}
