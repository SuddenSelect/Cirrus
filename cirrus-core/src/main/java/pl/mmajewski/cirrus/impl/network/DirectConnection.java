package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.exception.ConnectionFailCirrusException;

/**
 * Created by Maciej Majewski on 15/09/15.
 */
public class DirectConnection implements Connection {

    private ConnectionPool connectionPool = null;
    private Host remoteHost = null;
    private boolean alive = false;

    public DirectConnection(ConnectionPool connectionPool, Host remoteHost) {
        this.connectionPool = connectionPool;
        this.remoteHost = remoteHost;
    }

    @Override
    public void connect() throws ConnectionFailCirrusException {
        if(!alive) {
            //TODO connect
            alive = true;
        }
    }

    @Override
    public boolean isAlive() {
        //TODO test connection and set 'alive' accordingly
        return alive;
    }

    @Override
    public void kill() {
        //TODO disconnect
        alive = false;
    }

    @Override
    public ConnectionPool getParentConnectionPool() {
        return connectionPool;
    }

    @Override
    public void setParentConnectionPool(ConnectionPool parentPool) {
        this.connectionPool = parentPool;
    }

    @Override
    public Host getRemoteHost() {
        return remoteHost;
    }
}
