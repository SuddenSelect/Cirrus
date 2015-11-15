package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.binding.CirrusCoreFactory;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusCore {
    private static Logger logger = Logger.getLogger(CirrusCore.class.getName());

    private ContentStorage contentStorage = CirrusCoreFactory.Persistance.newContentStorage();
    private CirrusCoreEventHandler cirrusCoreEventHandler = new CirrusCoreEventHandler(this);
    private volatile boolean processEvents = true;
    private Thread processThread;

    public CirrusCore() {
        processThread = new Thread(cirrusCoreEventHandler);
        processThread.start();
    }

    public void stopProcessingEvents(){
        processEvents = false;
    }

    public CirrusEventHandler getCirrusCoreEventHandler(){
        return cirrusCoreEventHandler;
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

}
