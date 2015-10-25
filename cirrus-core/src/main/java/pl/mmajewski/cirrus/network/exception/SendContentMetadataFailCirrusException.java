package pl.mmajewski.cirrus.network.exception;

import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;

/**
 * Created by Maciej Majewski on 25/10/15.
 */
public class SendContentMetadataFailCirrusException extends NetworkCirrusException {
    public SendContentMetadataFailCirrusException(Throwable cause, Connection involvedConnection, ConnectionPool involvedConnectionPool) {
        super(cause, involvedConnection, involvedConnectionPool);
    }
}
