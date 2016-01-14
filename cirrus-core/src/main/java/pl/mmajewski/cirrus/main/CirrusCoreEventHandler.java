package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.common.util.CirrusIdGenerator;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;

import java.util.HashMap;
import java.util.Map;
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
    private BlockingQueue<String> failures = new LinkedBlockingQueue<>(30);
    private CirrusEventHandler appEventHandler;
    private CirrusCore parent;

    public CirrusCoreEventHandler(CirrusCore parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            logger.fine("CirrusCoreEventHandler started");
            while (parent.isProcessEvents() || hasAwaitingEvents()) {
                CirrusEvent evt = queue.poll(10, TimeUnit.MILLISECONDS);
                if (evt != null) {
                    try{
                        this.handle(evt);
                    }catch (Exception e){
                        logger.severe(e.getMessage());
                        e.printStackTrace();
                        ActionFailureCirrusEvent event = new ActionFailureCirrusEvent();
                        event.setException(e);
                        event.setMessage(e.getMessage());
                        this.handle(event);
                    }
                } else {
                    synchronized (queue) {
                        queue.notifyAll();
                    }
                }
            }
            logger.fine("CirrusCoreEventHandler stopped");
        } catch (InterruptedException e) {
            logger.fine("CirrusCoreEventHandler interrupted");
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


    @Override
    public void pushFailure(String failure){
        failures.add(failure);
    }

    @Override
    public String popFailure(){
        return failures.poll();
    }

    // Should be overriden
    private static String cirrusId = CirrusIdGenerator.generateHostId();
    @Override
    public String getLocalCirrusId() {
        return cirrusId;
    }

    private Map<String/*contentId*/, CirrusBlockingSequence<ContentPiece>> sinks = new HashMap<>();
    @Override
    public CirrusBlockingSequence<ContentPiece> getContentPieceSink(ContentMetadata metadata) {
        if(!sinks.containsKey(metadata.getContentId())){
            synchronized (sinks) {
                if(!sinks.containsKey(metadata.getContentId())) {
                    sinks.put(metadata.getContentId(), new CirrusBlockingSequence<>(metadata.getPiecesAmount()));
                }
            }
        }
        return sinks.get(metadata.getContentId());
    }

    @Override
    public void freeContentPieceSink(ContentMetadata metadata) {
        sinks.remove(metadata.getContentId());
    }
}
