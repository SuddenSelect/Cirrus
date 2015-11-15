package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.binding.CirrusCoreFactory;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.CoreServerInitializationCirrusException;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 07/11/15.
 */
public class CirrusCoreServer extends CirrusCoreEventHandler implements ServerCirrusEventHandler {
    private static Logger logger = Logger.getLogger(CirrusCoreServer.class.getName());

    private class Server extends Thread{
        private Socket client;

        public void setClient(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                logger.fine("CirrusCoreServer started");
                ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
                while (true) {
                    Object received = inputStream.readUnshared();
                    if(received != null){
                        if(received instanceof CirrusEvent) {
                            CirrusCoreServer.super.accept((CirrusEvent) received);
                        }else{
                            logger.warning("[CirrusCoreServer.Server] Received not-event: "+received.getClass());
                        }
                    }else{
                        logger.info("[CirrusCoreServer.Server] Read null");
                    }
                }
            } catch (EventHandlerClosingCirrusException|ClassNotFoundException|IOException e) {
                logger.warning("[CirrusCoreServer.Server] Stopping server: "+e.getMessage());
            } finally {
                synchronized (activeServers) {
                    activeServers.remove(this);
                }
                logger.fine("CirrusCoreServer stopped");
            }
        }
    }

    private class Listener implements Runnable{
        private ServerSocket serverSocket;

        public Listener(int port) throws IOException {
            this.serverSocket = new ServerSocket(port);
        }

        @Override
        public void run() {
            try{
                while (true){
                    Server server = new Server();
                    Socket client = serverSocket.accept();
                    server.setClient(client);
                    server.start();
                    synchronized (activeServers) {
                        activeServers.add(server);
                        logger.info("[CirrusCoreServer.Listener] Active threads: "+activeServers.size());
                    }
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }

        public void kill() throws InterruptedException {
            for(Server server : activeServers){
                server.interrupt();
            }
            for(Server server : activeServers){
                server.join();
            }
        }
    }

    private Set<Server> activeServers = new HashSet<>();
    private Listener listener;
    private Thread listenerThread;

    private HostStorage hostStorage = null;
    private AvailabilityStorage availabilityStorage = null;

    public CirrusCoreServer(CirrusCore parent, int port) {
        super(parent);
        try {
            this.listener = new Listener(port);
            this.listenerThread = new Thread(listener);
        } catch (IOException e) {
            new CoreServerInitializationCirrusException(e,Host.getLocalHost());
        }
    }

    @Override
    synchronized public void listen() {
        listenerThread.start();
    }

    @Override
    synchronized public void setHostStorage(HostStorage hostStorage) {
        this.hostStorage = hostStorage;
    }

    @Override
    synchronized public HostStorage getHostStorage() {
        return hostStorage;
    }

    @Override
    synchronized public void setAvailabilityStorage(AvailabilityStorage contentAvailability) {
        this.availabilityStorage = contentAvailability;
    }

    @Override
    synchronized public AvailabilityStorage getAvailabilityStorage() {
        return availabilityStorage;
    }
}
