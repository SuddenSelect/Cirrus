package pl.mmajewski.cirrus.common.exception;

import pl.mmajewski.cirrus.common.event.CirrusEvent;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public final class UnimplementedEventCirrusException extends RuntimeException{
    private CirrusEvent involvedEvent;

    public UnimplementedEventCirrusException(Throwable cause, CirrusEvent involvedEvent) {
        super(cause);
        this.involvedEvent = involvedEvent;
    }

    public UnimplementedEventCirrusException(CirrusEvent involvedEvent) {
        this.involvedEvent = involvedEvent;
    }

    public CirrusEvent getInvolvedEvent() {
        return involvedEvent;
    }

    public void setInvolvedEvent(CirrusEvent involvedEvent) {
        this.involvedEvent = involvedEvent;
    }

    public String getInvolvedEventClassName(){
        return involvedEvent==null? null : involvedEvent.getClass().getName();
    }
}
