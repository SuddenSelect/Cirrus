package pl.mmajewski.cirrus.common.exception;

import pl.mmajewski.cirrus.common.model.Host;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class CoreServerInitializationCirrusException extends CirrusException {

    public CoreServerInitializationCirrusException(Throwable cause, Host involvedHost) {
        super(cause, involvedHost, null, null);
    }

    public CoreServerInitializationCirrusException(Host involvedHost) {
        super(involvedHost, null, null);
    }
}
