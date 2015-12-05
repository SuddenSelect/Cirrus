package pl.mmajewski.cirrus.binding;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.common.util.CirrusIdGenerator;
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

import java.io.*;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 15/11/15.
 *
 * Class allowing easy implementation switching and singletonization.
 *
 *
 */
public class CirrusCoreFactory {
    private static final Logger logger = Logger.getLogger(CirrusCoreFactory.class.getName());

    private static Host localhost = null;

    public static CirrusCore newCirrusCore(InetAddress localAddress){
        return new CirrusCore(localAddress);
    }

    private static Host loadIdentity(){
        Host localhost = null;
        File identity = new File(".identity");
        if(identity.exists()){
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(identity));
                localhost = (Host) in.readObject();
                in.close();
            } catch (IOException e) {
                logger.warning(e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.severe(e.getMessage());
            } catch (ClassCastException e){
                logger.severe("Identity file malformed! "+e.getMessage());
            }
        }
        return localhost;
    }

    private static void saveIdentity(){
        Host localHost = getLocalhost();
        if(localHost!=null){
            try {
                File identity = new File(".identity");
                if (identity.exists()) {
                    identity.delete();
                }
                identity.createNewFile();
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(identity));
                out.writeObject(localHost);
                out.flush();
                out.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public static Host getLocalhost() {
        return localhost;
    }

    public static Host getLocalhost(InetAddress inetAddress) {
        if(localhost == null) {
            localhost = loadIdentity();
            if(localhost==null) {
                localhost = new Host();
                localhost.setPhysicalAddress(inetAddress);
                localhost.setCirrusId(CirrusIdGenerator.generateHostId());
                localhost.setFirstSeen(LocalDateTime.now());
                localhost.setLastSeen(LocalDateTime.now());
                localhost.setLatency(0);
                localhost.setPort(6465);//TODO make persistent
                localhost.setTags(new ArrayList<>(0));//TODO make persistent
                localhost.setLastUpdated(LocalDateTime.now());//TODO make persistent
                localhost.setAvailableContent(new HashSet<>());//TODO make persistent
                localhost.setSharedPiecesMap(new HashMap<>());
                saveIdentity();
            }else {
                localhost.setPhysicalAddress(inetAddress);
                localhost.setLastSeen(LocalDateTime.now());
            }
        }
        return localhost;
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
            cirrusCoreServer.setHostStorage(Persistance.newHostStorage(getLocalhost(localhost)));//TODO permanent localhost retrieval
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
