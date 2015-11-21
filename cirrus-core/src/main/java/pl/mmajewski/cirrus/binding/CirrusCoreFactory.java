package pl.mmajewski.cirrus.binding;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.impl.network.ClientDirectConnectionPool;
import pl.mmajewski.cirrus.impl.persistance.MemoryAvailabilityStorage;
import pl.mmajewski.cirrus.impl.persistance.MemoryContentStorage;
import pl.mmajewski.cirrus.impl.persistance.MemoryHostStorage;
import pl.mmajewski.cirrus.main.CirrusCore;
import pl.mmajewski.cirrus.main.CirrusCoreServer;
import pl.mmajewski.cirrus.network.Connection;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.net.InetAddress;

/**
 * Created by Maciej Majewski on 15/11/15.
 *
 * Class allowing easy implementation switching and singletonization.
 *
 *
 */
public class CirrusCoreFactory {

    public static CirrusCore newCirrusCore(InetAddress localAddress){
        return new CirrusCore(localAddress);
    }

    public static class Network {

        public static Connection newConnection() {
            return null;//binding stub
        }

        public static ConnectionPool newConnectionPool() {
            return new ClientDirectConnectionPool();//binding stub
        }
    }

    public static class Server {

        public static ServerCirrusEventHandler newCoreEventHandler(InetAddress localAddress, int port) {
            return new CirrusCoreServer(newCirrusCore(localAddress), port);//binding stub
        }

        public static ServerCirrusEventHandler newCoreEventHandler(CirrusCore cirrusCore, InetAddress localhost, int port) {
            CirrusCoreServer cirrusCoreServer = new CirrusCoreServer(cirrusCore, port);
            cirrusCoreServer.setAvailabilityStorage(Persistance.newAvailabilityStorage());
            cirrusCoreServer.setHostStorage(Persistance.newHostStorage(Host.newHost(localhost)));//TODO permanent localhost retrieval
            return cirrusCoreServer;//binding stub
        }
    }

    public static class Client {
        public static ClientEventConnection newEventConnection() {
            return null;//binding stub
        }

        public static CirrusEventPropagationStrategy newEventPropagationStrategy() {
            return null;//binding stub
        }
    }


    // Persistance
    public static class Persistance {
        public static ContentStorage newContentStorage() {
            return new MemoryContentStorage();//binding stub
        }

        public static AvailabilityStorage newAvailabilityStorage() {
            return new MemoryAvailabilityStorage();//binding stub
        }

        public static HostStorage newHostStorage(Host localhost) {
            return new MemoryHostStorage(localhost);//binding stub
        }
    }
}
