package pl.mmajewski.cirrus.network;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.exception.ConnectionFailCirrusException;

/**
 * Connections may have only one ConnectionPool parent.
 *
 * Created by Maciej Majewski on 29/10/14.
 */
public interface Connection {

    /**
     * Attempts to connect to remote host.
     * @throws ConnectionFailCirrusException thrown when connecting attempt have failed.
     */
    public void connect() throws ConnectionFailCirrusException;

    /**
     * Method verifying connection state, invoked by ConnectionPool parent.
     */
    public boolean isAlive();

    /**
     * Terminates connection.
     */
    public void kill();

    public ConnectionPool getParentConnectionPool();
    public void setParentConnectionPool(ConnectionPool parentPool);
    public Host getRemoteHost();
    public void setRemoteHost(Host remoteHost);
}
