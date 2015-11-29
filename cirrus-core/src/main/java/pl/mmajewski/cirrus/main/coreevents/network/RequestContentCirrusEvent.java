package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class RequestContentCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Host requester = null;
    private ContentMetadata contentMetadata = null;
    private Set<Integer> neededPieces = null;

    public void setRequester(Host requester) {
        this.requester = requester;
    }

    public void setContentMetadata(ContentMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public void setNeededPieces(Set<Integer> neededPieces) {
        this.neededPieces = neededPieces;
    }


    @Override
    public void event(ServerCirrusEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        ArrayList<ContentPiece> availablePieces = storage.getAvailablePieces(contentMetadata);
        availablePieces.removeIf(piece -> !neededPieces.contains(piece.getSequence()));

        DeliverContentCirrusEvent contentEvent = new DeliverContentCirrusEvent();
        contentEvent.setContentMetadata(contentMetadata);
        contentEvent.setContentPieces(availablePieces);

        ConnectionPool connectionPool = handler.getConnectionPool();
        try {
            ClientEventConnection connection = connectionPool.fetchConnection(requester);
            connection.sendEvent(contentEvent);
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
}
