package javax0.geci.api;

public class GeciException extends RuntimeException {
    public GeciException() {
        super();
    }

    public GeciException(String message, Object... args) {
        super(String.format(message, args));
    }

    public GeciException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeciException(Throwable cause) {
        super(cause);
    }

    protected GeciException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
