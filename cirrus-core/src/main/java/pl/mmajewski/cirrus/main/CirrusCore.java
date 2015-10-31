package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.binding.common.Persistance;
import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusCore {
    private static Logger logger = Logger.getLogger(CirrusCore.class.getName());

    private ContentStorage contentStorage = Persistance.newContentStorage();
    private CoreEventHandler coreEventHandler = this.new CoreEventHandler();
    private boolean processEvents = true;
    private Thread processThread;

    public CirrusCore() {
        processThread = new Thread(coreEventHandler);
        processThread.start();
    }

    public void stopProcessingEvents(){
        processEvents = false;
    }

    public CirrusEventHandler getCoreEventHandler(){
        return coreEventHandler;
    }

    public class CoreEventHandler implements CirrusEventHandler, Runnable {
        private BlockingQueue<CirrusEvent<CoreEventHandler>> queue = new LinkedBlockingQueue<>();
        private CirrusEventHandler appEventHandler;

        @Override
        public void run() {
            try {
                while (processEvents || hasAwaitingEvents()) {
                    CirrusEvent evt = queue.poll(10, TimeUnit.MILLISECONDS);
                    if(evt!=null){
                        System.err.println(evt.getClass().getName());
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
        public boolean hasAwaitingEvents() {
            return !queue.isEmpty();
        }

        @Override
        public void standby() throws InterruptedException {
            if(processEvents){
                logger.info("CORE: Waiting for thread to process events");
                synchronized (queue){
                    queue.wait();
                }
            }else {
                logger.info("CORE: Waiting for thread to finish by itself");
                processThread.join();
            }
        }

        @Override
        public void accept(CirrusEvent event) throws EventHandlerClosingCirrusException{
            if(processEvents){
                queue.add(event);
            }else{
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
            CirrusCore.this.contentStorage = contentStorage;
        }

        public ContentStorage getContentStorage() {
            return CirrusCore.this.contentStorage;
        }

    }
}
