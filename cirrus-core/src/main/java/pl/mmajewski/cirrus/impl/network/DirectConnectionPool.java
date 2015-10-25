package pl.mmajewski.cirrus.impl.network;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maciej Majewski on 15/09/15.
 */
public class DirectConnectionPool implements ConnectionPool{
    private int maxConnectionsToHost = 5;
    private int maxConnectionsInPool = 1000;
    private long connectionHealthCheckInterval = 1000;
    private Map<Host, DirectConnectionGroup> connectionGroupMap = new HashMap<>();

    @Override
    public void setMaxConnectionsToHost(int maxConnectionsToHost) {
        this.maxConnectionsToHost = maxConnectionsToHost;
    }

    @Override
    public int getMaxConnectionsToHost() {
        return maxConnectionsToHost;
    }

    @Override
    public void setMaxConnectionsInPool(int maxConnections) {
        this.maxConnectionsInPool = maxConnections;
    }

    @Override
    public int getMaxConnectionsInPool() {
        return maxConnectionsInPool;
    }

    @Override
    public long getConnectionHealthCheckInterval() {
        return connectionHealthCheckInterval;
    }

    @Override
    public Connection fetchDataConnection(Host remoteHost) throws NetworkCirrusException {
        DirectConnectionGroup connectionGroup = connectionGroupMap.get(remoteHost);
        if(connectionGroup!=null){
            return connectionGroup.getConnection();
        }
        return null;
    }

    @Override
    public void initialize(Collection<? extends Host> knownHosts) {
        for(Host host : knownHosts){
            addHost(host);
        }
    }

    @Override
    public void addHost(Host newHost) {
        if(!connectionGroupMap.containsKey(newHost)){
            connectionGroupMap.put(newHost, new DirectConnectionGroup(this, newHost));
        }
    }
}
