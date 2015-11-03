package pl.mmajewski.cirrus.network.exception;

import pl.mmajewski.cirrus.common.exception.CirrusException;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;

/**
 * Created by Maciej Majewski on 30/10/14.
 */
public class NetworkCirrusException extends CirrusException {
    private Connection involvedConnection = null;
    private ConnectionPool involvedConnectionPool = null;

    public NetworkCirrusException(Throwable cause) {
        super(cause, null, null, null);
    }

    public NetworkCirrusException(Throwable cause, Connection involvedConnection, ConnectionPool involvedConnectionPool) {
        super(cause, (involvedConnection != null ? involvedConnection.getRemoteHost() : null), null, null);
        this.involvedConnection = involvedConnection;
        this.involvedConnectionPool = involvedConnectionPool;
    }

    public NetworkCirrusException(Connection involvedConnection, ConnectionPool involvedConnectionPool) {
        this.involvedConnection = involvedConnection;
        this.involvedConnectionPool = involvedConnectionPool;
    }

    protected NetworkCirrusException() {

    }

    public ConnectionPool getInvolvedConnectionPool() {
        return involvedConnectionPool;
    }

    public void setInvolvedConnectionPool(ConnectionPool involvedConnectionPool) {
        this.involvedConnectionPool = involvedConnectionPool;
    }

    public Connection getInvolvedConnection() {
        return involvedConnection;
    }

    public void setInvolvedConnection(Connection involvedConnection) {
        this.involvedConnection = involvedConnection;
        setInvolvedHost(involvedConnection.getRemoteHost());
    }

}
