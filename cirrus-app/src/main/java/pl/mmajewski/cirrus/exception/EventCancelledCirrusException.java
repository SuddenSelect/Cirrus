package pl.mmajewski.cirrus.exception;

/**
 * Created by Maciej Majewski on 2015-02-07.
 */
public class EventCancelledCirrusException extends RuntimeException {
    public EventCancelledCirrusException() {
    }

    public EventCancelledCirrusException(String message) {
        super(message);
    }

    public EventCancelledCirrusException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventCancelledCirrusException(Throwable cause) {
        super(cause);
    }

    public EventCancelledCirrusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
