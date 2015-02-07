package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEventHandler;
import pl.mmajewski.cirrus.impl.persistance.MemoryContentStorage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusBasicApp  {
    private static Logger logger = Logger.getLogger(CirrusBasicApp.class.getName());

    private ContentStorage prepared = new MemoryContentStorage();
    private CirrusCore core = new CirrusCore();
    private AppEventHandler appEventHandler = this.new AppEventHandler();
    private volatile boolean processEvents = true;
    private Thread processThread;

    public CirrusBasicApp(){
        processThread = new Thread(appEventHandler);
        processThread.start();
        core.getCoreEventHandler().setAppEventHandler(appEventHandler);
    }

    public AppEventHandler getAppEventHandler() {
        return appEventHandler;
    }

    public void stopProcessingEvents(){
        processEvents=false;
        core.stopProcessingEvents();
    }

    private void resetPreparedContentStorage(){
        prepared = new MemoryContentStorage();
    }

    public class AppEventHandler implements CirrusAppEventHandler, Runnable {
        private BlockingQueue<CirrusEvent<AppEventHandler>> queue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            try {
                while (processEvents || hasAwaitingEvents()) {
                    CirrusEvent evt = queue.poll(20, TimeUnit.MILLISECONDS);
                    if(evt!=null){
                        this.handle(evt);
                    }else{
                        synchronized (queue){
                            queue.notifyAll();
                        }
                    }
                }
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
                processThread.join();
            }
        }

        @Override
        public void setContentStorage(ContentStorage contentStorage) {
            throw new UnsupportedOperationException();
        }

        public ContentStorage getContentStorage() {
            return CirrusBasicApp.this.prepared;
        }

        public void resetStorage(){
            CirrusBasicApp.this.resetPreparedContentStorage();
        }

        public CirrusEventHandler getCoreEventHandler(){
            return core.getCoreEventHandler();
        }
    }

}
