package pl.mmajewski.cirrus.main.coreevents.storage;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.impl.client.EqualDiffusionStrategy;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.SendAvailabilityPropagationCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.StoreContentCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusDiffusionStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class BalanceAndDiffuseStorageCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private ContentStorage toCommit;

    public void setStorageToCommit(ContentStorage toCommit) {
        this.toCommit = toCommit;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {

        Set<ContentAvailability> availabilities = new HashSet<>();

        CirrusDiffusionStrategy<StoreContentCirrusEvent> diffusionStrategy = new EqualDiffusionStrategy();
        Map<Host, StoreContentCirrusEvent> targets = diffusionStrategy.getTargets(toCommit, handler.getHostStorage());

        ConnectionPool connectionPool = handler.getConnectionPool();

        for(Host host : targets.keySet()){
            StoreContentCirrusEvent storeEvent = targets.get(host);
            try {
                if (host.equals(Host.getLocalhost())) {
                    handler.accept(storeEvent);
                } else {
                    ClientEventConnection connection = connectionPool.fetchConnection(host);
                    connection.sendEvent(storeEvent);
                }

                for(ContentMetadata metadata : storeEvent.getContentMap().keySet()) {
                    Set<Integer> pieces = new HashSet<>();
                    for(ContentPiece piece : storeEvent.getContentMap().get(metadata)){
                        pieces.add(piece.getSequence());
                    }
                    ContentAvailability availability = new ContentAvailability();
                    availability.setHolderCirrusId(host.getCirrusId());
                    availability.setContentId(metadata.getContentId());
                    availability.setPiecesSequenceNumbers(pieces);
                    availabilities.add(availability);
                }
            } catch (EventHandlerClosingCirrusException e) {
                e.printStackTrace();
            } catch (NetworkCirrusException e) {
                ActionFailureCirrusEvent failureEvent = new ActionFailureCirrusEvent();
                failureEvent.setException(e);
                failureEvent.setMessage(e.getMessage());
                e.printStackTrace();
                try {
                    handler.accept(failureEvent);
                } catch (EventHandlerClosingCirrusException e1) {
                    e.printStackTrace();
                }
            }
        }

        SendAvailabilityPropagationCirrusEvent availabilityPropagationEvent = new SendAvailabilityPropagationCirrusEvent();
        availabilityPropagationEvent.setAvailabilities(availabilities);

        try {
            handler.accept(availabilityPropagationEvent);
        } catch (EventHandlerClosingCirrusException e) {
            e.printStackTrace();
        }

    }
}
