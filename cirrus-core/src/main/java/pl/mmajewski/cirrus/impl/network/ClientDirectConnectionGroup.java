package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.exception.ConnectionFailCirrusException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Maciej Majewski on 15/09/15.
 */
/*package*/ class ClientDirectConnectionGroup implements Connection {
    private class HealthCheck implements Runnable{
        @Override
        public void run() {
            Set<Connection> toDel = new HashSet<>();
            try {
                while(isAlive()) {
                    for(Connection connection : connections) {
                        if(!connection.isAlive()) {
                            toDel.add(connection);
                            connection.kill();
                        }
                    }
                    connections.removeAll(toDel);
                    if(connections.isEmpty()){
                        alive = false;
                        break;
                    }else{
                        connect();
                    }
                    toDel.clear();
                    Thread.sleep(connectionPool.getConnectionHealthCheckInterval());
                }
            } catch (InterruptedException e) {
            } catch (ConnectionFailCirrusException e) {
                e.printStackTrace();
            }
        }
    }
    private Thread healthCheckThread = null;
    private HealthCheck healthCheck = null;
    private boolean alive = false;
    private ConnectionPool connectionPool = null;
    private Host remoteHost;
    private Queue<ClientDirectConnection> connections = new LinkedList<>();

    public ClientDirectConnectionGroup(ConnectionPool connectionPool, Host remoteHost) {
        this.connectionPool = connectionPool;
        this.remoteHost = remoteHost;
        this.healthCheck = new HealthCheck();
        this.healthCheckThread = new Thread(healthCheck);
    }

    @Override
    public void connect() throws ConnectionFailCirrusException {
        boolean startHealthCheck = connections.isEmpty();
        for (int i = connections.size(); i < connectionPool.getMaxConnectionsToHost(); i++) {
            ClientDirectConnection connection = new ClientDirectConnection(connectionPool, remoteHost);
            connections.add(connection);
            connection.connect();
        }
        if(startHealthCheck){
            this.healthCheckThread.start();
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void kill() {
        alive = false;
        if(healthCheckThread!=null){
            healthCheckThread.interrupt();
        }
        for(Connection connection : connections){
            connection.kill();
        }
    }

    @Override
    public ConnectionPool getParentConnectionPool() {
        return connectionPool;
    }

    @Override
    public void setParentConnectionPool(ConnectionPool parentPool) {
        for(Connection connection : connections){
            connection.setParentConnectionPool(parentPool);
        }
        connectionPool = parentPool;
    }

    @Override
    public Host getRemoteHost() {
        return remoteHost;
    }

    public Iterable<ClientDirectConnection> getAllConnections(){
        return connections;
    }

    public ClientDirectConnection getConnection(){
        ClientDirectConnection connection = connections.remove();
        connections.add(connection);
        return connection;
    }
}
