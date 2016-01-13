package pl.mmajewski.cirrus.main.coreevents.storage;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.send.SendAvailabilityPropagationCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.StoreContentCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusDiffusionStrategy;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class BalanceAndDiffuseStorageCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {
    private static final Logger logger = Logger.getLogger(BalanceAndDiffuseStorageCirrusEvent.class.getName());

    private ContentStorage storageToCommit;
    private CirrusDiffusionStrategy<StoreContentCirrusEvent> diffusionStrategy = null;
    private CirrusEventPropagationStrategy availabilityPropagationStrategy = null;

    public void setStorageToCommit(ContentStorage storageToCommit) {
        this.storageToCommit = storageToCommit;
    }

    public void setDiffusionStrategy(CirrusDiffusionStrategy<StoreContentCirrusEvent> diffusionStrategy) {
        this.diffusionStrategy = diffusionStrategy;
    }

    public void setAvailabilityPropagationStrategy(CirrusEventPropagationStrategy availabilityPropagationStrategy) {
        this.availabilityPropagationStrategy = availabilityPropagationStrategy;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        long startTime = System.currentTimeMillis();//Test metrics

        Set<ContentAvailability> availabilities = new HashSet<>();

        Map<Host, StoreContentCirrusEvent> targets = diffusionStrategy.getTargets(storageToCommit, handler.getHostStorage());

        ConnectionPool connectionPool = handler.getConnectionPool();

        Set<String> trace = new HashSet<>();
        for(Host target : targets.keySet()){
            trace.add(target.getCirrusId());
        }
        for(Host host : targets.keySet()){
            StoreContentCirrusEvent storeEvent = targets.get(host);
            storeEvent.addTrace(trace);
            storeEvent.addTrace(this.getTrace());
            storeEvent.addTrace(handler.getLocalCirrusId());
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
        availabilityPropagationEvent.setPropagationStrategy(availabilityPropagationStrategy);

        try {
            handler.accept(availabilityPropagationEvent);
        } catch (EventHandlerClosingCirrusException e) {
            e.printStackTrace();
        }

        //Test metrics
        long uploadTime = System.currentTimeMillis() - startTime;
        StringBuilder contents = new StringBuilder();
        boolean skipComma = true;
        for(ContentMetadata metadata : storageToCommit.getAllContentMetadata()){
            if(skipComma){
                skipComma=false;
            }else{
                contents.append(", ");
            }
            contents.append(metadata.getContentId());
        }
        logger.info("[TIME] UPLOAD_FILE: "+uploadTime+"ms ("+contents.toString()+")");
    }
}
