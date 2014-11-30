package pl.mmajewski.cirrus.common.event;

import pl.mmajewski.cirrus.common.exception.UnimplementedEventCirrusException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 29/10/14.
 */
public abstract class CirrusEvent <CEH extends CirrusEventHandler> implements Serializable{
    private static final long serialVersionUID = 1681266000004L;

    private LocalDateTime creationTime;
    private String eventId;
    private Set<String> trace = new HashSet<String>();

    /**
     * Method implementing the functionality of CirrusEvent
     */
    public void event(CEH handler) {
        throw new UnimplementedEventCirrusException(this);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * For information about hosts who already received Event.
     * @param cirrusId ID of the host who received
     */
    public void addTrace(String cirrusId){
        trace.add(cirrusId);
    }

    /**
     * For retrieving set of visited hosts.
     * @return
     */
    public Set<String> getTrace(){
        return trace;
    }

    @Override
    public boolean equals(Object e){
        // instanceof covers null case as false
        return e instanceof CirrusEvent && eventId.equals(((CirrusEvent)e).eventId) && creationTime.isEqual(((CirrusEvent)e).creationTime);
    }

}
