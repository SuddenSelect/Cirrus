package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.impl.client.BroadcastPropagationStrategy;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class SendAvailabilityPropagationCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<ContentAvailability> availabilities = null;
    private CirrusEventPropagationStrategy propagationStrategy = null;

    public void setAvailabilities(Set<ContentAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    public void setPropagationStrategy(CirrusEventPropagationStrategy propagationStrategy) {
        this.propagationStrategy = propagationStrategy;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        SendAvailabilityPropagationCirrusEvent propagationEvent = new SendAvailabilityPropagationCirrusEvent();
//        propagationEvent.setAvailabilities(availabilities);
        propagationEvent.addTrace(this.getTrace());
//        propagationEvent.addTrace(handler.getLocalCirrusId());

        Set<Host> targets = propagationStrategy.getTargets(handler.getHostStorage(), propagationEvent);

        Set<String> trace = new HashSet<>();
        for(Host target : targets){
            trace.add(target.getCirrusId());
        }
        propagationEvent.addTrace(trace);

        ConnectionPool connectionPool = handler.getConnectionPool();
        for(Host host : targets){
            try {
                if(host.equals(handler.getHostStorage().fetchLocalHost())){
//                    handler.accept(propagationEvent);
                    updateLocalAvailabilities(handler);
                } else {
                    ClientEventConnection connection = connectionPool.fetchConnection(host);
                    connection.sendEvent(propagationEvent);
                }
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

    public void updateLocalAvailabilities(ServerCirrusEventHandler handler) {
        handler.getAvailabilityStorage().addAvailability(availabilities);

        HostStorage hostStorage = handler.getHostStorage();
        Set<Host> updated = new HashSet<>();
        for(ContentAvailability availability : availabilities){
            Host holder = hostStorage.fetchHost(availability.getHolderCirrusId());
            if(!holder.getSharedPiecesMap().containsKey(availability.getContentId())){
                holder.getSharedPiecesMap().put(availability.getContentId(), new HashSet<>());
            }
            holder.getSharedPiecesMap().get(availability.getContentId()).clear();
            holder.getSharedPiecesMap().get(availability.getContentId()).addAll(availability.getPiecesSequenceNumbers());

            holder.getAvailableContent().add(availability.getContentId());

            updated.add(holder);
        }
        hostStorage.updateHosts(updated);
    }
}
