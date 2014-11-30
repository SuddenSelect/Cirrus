package pl.mmajewski.cirrus.network.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.Map;
import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class ContentPiecesMigrationEvent extends CirrusEvent<ServerCirrusEventHandler> {
    private static final long serialVersionUID = 1681266000007L;

    private Set<ContentAvailability> contentAvailabilities;

    @Override
    public void event(ServerCirrusEventHandler handler){
        handler.getAvailabilityStorage().addAvailability(contentAvailabilities);
    }
}
