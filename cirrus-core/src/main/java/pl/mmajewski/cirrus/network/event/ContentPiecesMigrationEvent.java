package pl.mmajewski.cirrus.network.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class ContentPiecesMigrationEvent extends CirrusEvent<ServerCirrusEventHandler> implements Serializable {
    private static final long serialVersionUID = 1681266000007L;

    private Set<ContentAvailability> contentAvailabilities;

    public void setContentAvailabilities(Set<ContentAvailability> contentAvailabilities) {
        this.contentAvailabilities = contentAvailabilities;
    }

    @Override
    public void event(ServerCirrusEventHandler handler){
        handler.getAvailabilityStorage().addAvailability(contentAvailabilities);
    }
}
