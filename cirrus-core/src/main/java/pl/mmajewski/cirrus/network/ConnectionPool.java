package pl.mmajewski.cirrus.network;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

import java.util.Collection;

/**
 * Handler of program Connections. It's duty is to maintain
 * healthy and living pool of connections through periodically invoked Connection's isAlive method.
 * @todo informing the planner about host statuses
 * Created by Maciej Majewski on 29/10/14.
 */
public interface ConnectionPool {
    /* Parametrization */
    public void setMaxConnectionsToHost(int maxConnectionsToHost);
    public int  getMaxConnectionsToHost();
    public void setMaxConnectionsInPool(int maxConnections);
    public int  getMaxConnectionsInPool();
    public long getConnectionHealthCheckInterval();
    /* *************** */

    /**
     * Sets event hander to which all failures will be reported.
     * @param handler
     */
    public void setParentEventHandler(CirrusEventHandler handler);

    /**
     * Returns existing or new Connection to remote Host.
     * @param <E> type of managed connection
     * @param remoteHost model descriptor of the Host
     * @return valid Connection
     * @throws NetworkCirrusException when connecting or network itself have failed
     */
    public <E extends Connection> E fetchConnection(Host remoteHost) throws NetworkCirrusException;

    /**
     * Initializes pool with connections to known hosts.
     * @param knownHosts collection of hosts to handle connections to
     */
    public void initialize(Collection<? extends Host> knownHosts);

    /**
     * Adds new host to which connection pool should begin maintaining connections.
     * @param newHost
     */
    public void addHost(Host newHost);

    /**
     * Kills all connections to given host and stops maintaining connections to it.
     * @param host
     */
    public void removeHost(Host host);
}
