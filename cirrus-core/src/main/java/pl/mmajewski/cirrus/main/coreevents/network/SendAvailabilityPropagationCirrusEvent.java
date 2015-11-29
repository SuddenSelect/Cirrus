package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.impl.client.BroadcastPropagationStrategy;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class SendAvailabilityPropagationCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<ContentAvailability> availabilities = null;

    public void setAvailabilities(Set<ContentAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        SendAvailabilityPropagationCirrusEvent propagationEvent = new SendAvailabilityPropagationCirrusEvent();
        propagationEvent.addTrace(this.getTrace());
        propagationEvent.addTrace(handler.getLocalCirrusId());

        CirrusEventPropagationStrategy propagationStrategy = new BroadcastPropagationStrategy<SendAvailabilityPropagationCirrusEvent>();
        Set<Host> targets = propagationStrategy.getTargets(handler.getHostStorage(), propagationEvent);

        Set<String> trace = new HashSet<>();
        for(Host target : targets){
            trace.add(target.getCirrusId());
        }
        propagationEvent.addTrace(trace);

        ConnectionPool connectionPool = handler.getConnectionPool();
        for(Host host : targets){
            try {
                ClientEventConnection connection = connectionPool.fetchConnection(host);
                connection.sendEvent(propagationEvent);
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
