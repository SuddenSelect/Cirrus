package pl.mmajewski.cirrus.network.exception;

import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;

/**
 * Created by Maciej Majewski on 29/10/14.
 */
public class ConnectionFailCirrusException extends NetworkCirrusException {
    public ConnectionFailCirrusException(Throwable cause, Connection involvedConnection, ConnectionPool involvedConnectionPool) {
        super(cause, involvedConnection, involvedConnectionPool);
    }
}
