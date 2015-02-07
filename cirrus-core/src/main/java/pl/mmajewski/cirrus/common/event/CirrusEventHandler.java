package pl.mmajewski.cirrus.common.event;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.exception.EventHandlerMismatchedCirrusException;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

/**
 * Handler interface for event-visitors with suggested implementation
 * Created by Maciej Majewski on 09/11/14.
 */
public interface CirrusEventHandler {

    /**
     * Accepts event for asynchronous processing putting in queue for events to be handled.
     * @param event queued event
     */
    default public void accept(CirrusEvent event) throws EventHandlerClosingCirrusException {
        throw new RuntimeException("Class "+this.getClass().getName()+" does not implement asynchronous event handling");
    }

    /**
     * Checks if the handler currently has any queued events for processing in the background
     * @return true when event queue is not empty
     */
    public boolean hasAwaitingEvents();

    /**
     * Blocks invoking threads until processing of all queued events is finished.
     * Notifies all.
     */
    public void standby() throws InterruptedException;

    /**
     * Visitor behavior implementation. Immediate execution.
     * @param event Event instance of type bound to EH
     */
    default public void handle(CirrusEvent<CirrusEventHandler> event){
        try {
            event.event(this);
        }catch (ClassCastException e){
            throw new EventHandlerMismatchedCirrusException(e,event,this);
        }
    }

    /**
     * Sets handler for the AppEvents
     * @param handler
     */
    public void setAppEventHandler(CirrusEventHandler handler);

    /**
     * Getter for the AppEvent handler
     * @return handler for the AppEvents
     */
    public CirrusEventHandler getAppEventHandler();

    /**
     * Propagates event to the application level for handling.
     * Should be first thing to be called by an event.
     * @param event Event instance of type bound to EH
     */
    default public void appEvent(CirrusEvent<CirrusEventHandler> event){
        getAppEventHandler().handle(event);
    }

    /**
     * Sets content storage for updating content data.
     * @param contentStorage
     */
    public void setContentStorage(ContentStorage contentStorage);

    /**
     * ContentStorage getter.
     * @return content storage if has been set, null otherwise.
     */
    public ContentStorage getContentStorage();
}
