package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 07/11/15.
 */
public class CirrusCoreEventHandler implements CirrusEventHandler, Runnable {
    private static Logger logger = Logger.getLogger(CirrusCoreEventHandler.class.getName());

    private BlockingQueue<CirrusEvent<CirrusCoreEventHandler>> queue = new LinkedBlockingQueue<>();
    private CirrusEventHandler appEventHandler;
    private CirrusCore parent;

    public CirrusCoreEventHandler(CirrusCore parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            while (parent.isProcessEvents() || hasAwaitingEvents()) {
                CirrusEvent evt = queue.poll(10, TimeUnit.MILLISECONDS);
                if (evt != null) {
                    System.err.println(evt.getClass().getName());
                    this.handle(evt);
                } else {
                    synchronized (queue) {
                        queue.notifyAll();
                    }
                }
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean hasAwaitingEvents() {
        return !queue.isEmpty();
    }

    @Override
    public void standby() throws InterruptedException {
        if (parent.isProcessEvents()) {
            logger.info("CORE: Waiting for thread to process events");
            synchronized (queue) {
                queue.wait();
            }
        } else {
            logger.info("CORE: Waiting for thread to finish by itself");
            parent.getProcessThread().join();
        }
    }

    @Override
    public void accept(CirrusEvent event) throws EventHandlerClosingCirrusException {
        if (parent.isProcessEvents()) {
            queue.add(event);
        } else {
            throw new EventHandlerClosingCirrusException();
        }
    }

    @Override
    public void setAppEventHandler(CirrusEventHandler handler) {
        this.appEventHandler = handler;
    }

    @Override
    public CirrusEventHandler getAppEventHandler() {
        return appEventHandler;
    }

    @Override
    public void setContentStorage(ContentStorage contentStorage) {
        parent.setContentStorage(contentStorage);
    }

    public ContentStorage getContentStorage() {
        return parent.getContentStorage();
    }

}
