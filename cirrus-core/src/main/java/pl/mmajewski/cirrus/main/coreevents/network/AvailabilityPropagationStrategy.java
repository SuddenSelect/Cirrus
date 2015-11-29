package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class AvailabilityPropagationStrategy extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<ContentAvailability> availabilities = null;

    public void setAvailabilities(Set<ContentAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        handler.getAvailabilityStorage().addAvailability(availabilities);

        for(ContentAvailability availability : availabilities){
            Host host = handler.getHostStorage().fetchHost(availability.getHolderCirrusId());
            host.getAvailableContent().add(availability.getContentId());
            host.getSharedPieces(availability.getContentId()).addAll(availability.getPiecesSequenceNumbers());
        }
    }
}
