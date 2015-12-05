package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.binding.CirrusCoreFactory;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusCore {
    private static Logger logger = Logger.getLogger(CirrusCore.class.getName());

    private ContentStorage contentStorage = CirrusCoreFactory.Persistance.newContentStorage();
    private ServerCirrusEventHandler cirrusCoreEventHandler;
    private volatile boolean processEvents = true;
    private Thread processThread;
    private InetAddress localAddress;

    public CirrusCore(InetAddress localAddress) {
        this.localAddress = localAddress;
        cirrusCoreEventHandler = CirrusCoreFactory.Server.newCoreEventHandler(this, localAddress, 6465);
        processThread = new Thread((Runnable) cirrusCoreEventHandler);
        processThread.start();
        cirrusCoreEventHandler.listen();
    }

    public void stopProcessingEvents(){
        processEvents = false;
        cirrusCoreEventHandler.kill();
    }

    public CirrusEventHandler getCirrusCoreEventHandler(){
        return cirrusCoreEventHandler;
    }

    public AvailabilityStorage getAvailabilityStorage(){
        return cirrusCoreEventHandler.getAvailabilityStorage();
    }

    public HostStorage getHostStorage(){
        return cirrusCoreEventHandler.getHostStorage();
    }

    public ContentStorage getContentStorage() {
        return contentStorage;
    }

    public boolean isProcessEvents() {
        return processEvents;
    }

    public Thread getProcessThread() {
        return processThread;
    }

    public void setContentStorage(ContentStorage contentStorage) {
        this.contentStorage = contentStorage;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }
}
