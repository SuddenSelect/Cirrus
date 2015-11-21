package pl.mmajewski.cirrus.common.exception;

/**
 * Created by Maciej Majewski on 2015-02-07.
 */
public class EventHandlerClosingCirrusException extends CirrusException {
    public EventHandlerClosingCirrusException(Throwable cause) {
        super(cause, null, null, null);
    }

    public EventHandlerClosingCirrusException() {
    }
}
