package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.binding.CirrusCoreFactory;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.common.util.CirrusBlockingSequence;
import pl.mmajewski.cirrus.event.CirrusAppEventHandler;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusBasicApp  {
    private static Logger logger = Logger.getLogger(CirrusBasicApp.class.getName());

    private ContentStorage contentStorage = CirrusCoreFactory.Persistance.newContentStorage();
    private CirrusCore core;
    private AppEventHandler appEventHandler = this.new AppEventHandler();
    private volatile boolean processEvents = true;
    private Thread processThread;

    public CirrusBasicApp(InetAddress localAddress){
        core = CirrusCoreFactory.newCirrusCore(localAddress);
        processThread = new Thread(appEventHandler);
        processThread.start();
        core.getCirrusCoreEventHandler().setAppEventHandler(appEventHandler);
    }

    public AppEventHandler getAppEventHandler() {
        return appEventHandler;
    }

    public void stopProcessingEvents(){
        processEvents=false;
        core.stopProcessingEvents();
    }

    private void resetPreparedContentStorage(){
        contentStorage = CirrusCoreFactory.Persistance.newContentStorage();
    }

    public void accept(CirrusEvent event) throws EventHandlerClosingCirrusException {
        getAppEventHandler().getCoreEventHandler().accept(event);
    }

    public class AppEventHandler implements CirrusAppEventHandler, Runnable {
        private BlockingQueue<CirrusEvent<AppEventHandler>> queue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            try {
                logger.fine("AppEventHandler started");
                while (processEvents || hasAwaitingEvents()) {
                    CirrusEvent evt = queue.poll(20, TimeUnit.MILLISECONDS);
                    if(evt!=null){
                        logger.fine("Event: "+evt.getClass());
                        try {
                            this.handle(evt);
                        }catch (Exception e){
                            logger.severe(e.getMessage());
                            ActionFailureCirrusEvent event = new ActionFailureCirrusEvent();
                            event.setException(e);
                            event.setMessage(e.getMessage());
                            this.handle(event);
                        }
                    }else{
                        synchronized (queue){
                            queue.notifyAll();
                        }
                    }
                }
                logger.fine("AppEventHandler stopped");
            } catch (InterruptedException e) {}
        }

        @Override
        public void accept(CirrusEvent event) throws EventHandlerClosingCirrusException{
            if(processEvents) {
                queue.add(event);
            }else{
                throw new EventHandlerClosingCirrusException();
            }
        }

        @Override
        public boolean hasAwaitingEvents() {
            return !queue.isEmpty();
        }

        @Override
        public void standby() throws InterruptedException {
            if(processEvents){
                logger.info("APP: Waiting for thread to process events");
                synchronized (queue){
                    queue.wait();
                }
            }else {
                logger.info("APP: Waiting for thread to finish by itself");
                processThread.join(10000);
            }
        }

        @Override
        public void setContentStorage(ContentStorage contentStorage) {
            throw new UnsupportedOperationException();
        }

        public HostStorage getHostStorage() {
            return core.getHostStorage();
        }

        public AvailabilityStorage getAvailabilityStorage() {
            return core.getAvailabilityStorage();
        }

        public ContentStorage getContentStorage() {
            return CirrusBasicApp.this.contentStorage;
        }

        public void resetStorage(){
            CirrusBasicApp.this.resetPreparedContentStorage();
        }

        public CirrusEventHandler getCoreEventHandler(){
            return core.getCirrusCoreEventHandler();
        }

        @Override
        public void pushFailure(String failure) {
            getCoreEventHandler().pushFailure(failure);
        }

        @Override
        public String popFailure() {
            return getCoreEventHandler().popFailure();
        }

        @Override
        public String getLocalCirrusId() {
            return getCoreEventHandler().getLocalCirrusId();
        }

        @Override
        public CirrusBlockingSequence<ContentPiece> getContentPieceSink(ContentMetadata metadata) {
            return getCoreEventHandler().getContentPieceSink(metadata);
        }

        @Override
        public void freeContentPieceSink(ContentMetadata metadata) {
            getCoreEventHandler().freeContentPieceSink(metadata);
        }
    }

}
