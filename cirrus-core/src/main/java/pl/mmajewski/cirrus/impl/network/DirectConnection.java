package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.exception.ConnectionFailCirrusException;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Maciej Majewski on 15/09/15.
 */
/*package*/ class DirectConnection implements Connection {

    private ConnectionPool connectionPool = null;
    private Host remoteHost = null;
    private boolean alive = false;
    private Socket socket = null;

    public DirectConnection(ConnectionPool connectionPool, Host remoteHost) {
        this.connectionPool = connectionPool;
        this.remoteHost = remoteHost;
    }

    @Override
    public void connect() throws ConnectionFailCirrusException {
        try {
            if (!alive) {
                socket = new Socket(remoteHost.getPhysicalAddress(), remoteHost.getPort());
                socket.setTrafficClass(0x10);
                alive = true;
            }
        } catch (Exception e) {
            throw new ConnectionFailCirrusException(e,this,connectionPool);
        }
    }

    @Override
    public boolean isAlive() {
        return alive && socket.isConnected();
    }

    @Override
    public void kill() {
        try {
            if(alive) {
                socket.close();
                alive = false;
            }
        } catch (IOException e) {
            e.printStackTrace();//TODO remove
        }
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

    protected Socket getSocket(){
        return socket;
    }
}
