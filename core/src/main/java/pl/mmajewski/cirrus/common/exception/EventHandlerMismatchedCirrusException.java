package pl.mmajewski.cirrus.common.exception;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public final class EventHandlerMismatchedCirrusException  extends RuntimeException {
    private CirrusEvent involvedEvent;
    private CirrusEventHandler involvedHandler;

    public EventHandlerMismatchedCirrusException(Throwable cause, CirrusEvent involvedEvent, CirrusEventHandler involvedHandler) {
        super(cause);
        this.involvedEvent = involvedEvent;
        this.involvedHandler = involvedHandler;
    }

    public EventHandlerMismatchedCirrusException(CirrusEvent involvedEvent, CirrusEventHandler involvedHandler) {
        this.involvedEvent = involvedEvent;
        this.involvedHandler = involvedHandler;
    }

    public CirrusEvent getInvolvedEvent() {
        return involvedEvent;
    }

    public void setInvolvedEvent(CirrusEvent involvedEvent) {
        this.involvedEvent = involvedEvent;
    }

    public CirrusEventHandler getInvolvedHandler() {
        return involvedHandler;
    }

    public void setInvolvedHandler(CirrusEventHandler involvedHandler) {
        this.involvedHandler = involvedHandler;
    }

    public String getInvolvedEventClassName(){
        return involvedEvent==null? null : involvedEvent.getClass().getName();
    }

    public String getInvolvedHandlerClassName(){
        return involvedHandler==null? null : involvedHandler.getClass().getName();
    }

}
