package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class ContentPiecesMigrationEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<ContentAvailability> contentAvailabilities;

    public void setContentAvailabilities(Set<ContentAvailability> contentAvailabilities) {
        this.contentAvailabilities = contentAvailabilities;
    }

    @Override
    public void event(ServerCirrusEventHandler handler){
        handler.getAvailabilityStorage().addAvailability(contentAvailabilities);
    }
}
