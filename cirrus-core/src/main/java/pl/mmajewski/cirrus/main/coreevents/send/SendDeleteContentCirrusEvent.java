package pl.mmajewski.cirrus.main.coreevents.send;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.impl.client.BroadcastPropagationStrategy;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.DeleteContentCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.MetadataPropagationCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Maciej Majewski on 30/12/15.
 */
public class SendDeleteContentCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private List<ContentMetadata> contentMetadataList;
    private CirrusEventPropagationStrategy propagationStrategy = new BroadcastPropagationStrategy<DeleteContentCirrusEvent>();

    public void setContentMetadataList(List<ContentMetadata> contentMetadataList) {
        this.contentMetadataList = contentMetadataList;
    }

    public void setPropagationStrategy(CirrusEventPropagationStrategy propagationStrategy) {
        this.propagationStrategy = propagationStrategy;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        DeleteContentCirrusEvent deleteEvent = new DeleteContentCirrusEvent();
        deleteEvent.setContentMetadataList(contentMetadataList);
        deleteEvent.addTrace(handler.getLocalCirrusId());
        deleteEvent.addTrace(this.getTrace());

        Set<Host> targets = propagationStrategy.getTargets(handler.getHostStorage(), deleteEvent);
        Set<String> trace = new HashSet<>();
        for(Host target : targets){
            trace.add(target.getCirrusId());
        }
        deleteEvent.addTrace(trace);
        for(Host host : targets) {
            ConnectionPool connectionPool = handler.getConnectionPool();
            connectionPool.addHost(host);
            try {
                ClientEventConnection connection = connectionPool.fetchConnection(host);
                connection.sendEvent(deleteEvent);
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
}
