package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.impl.client.MetadataBroadcastPropagationStrategy;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.event.FetchContentCirrusEvent;
import pl.mmajewski.cirrus.network.event.HostCirrusEvent;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 21/11/15.
 */
public class SignupCirrusEvent extends HostCirrusEvent {

    private Set<Host> joiningHosts = null;

    public void setJoiningHosts(Set<Host> joiningHosts) {
        this.joiningHosts = joiningHosts;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        super.event(handler);

        HostCirrusEvent hostShareEvent = new HostCirrusEvent();
        hostShareEvent.setSharedHosts(handler.getHostStorage().fetchAllHosts());
        hostShareEvent.addTrace(handler.getLocalCirrusId());

        FetchContentCirrusEvent metadataShareEvent = new FetchContentCirrusEvent();
        metadataShareEvent.setSharedMetadata(handler.getContentStorage().getAllContentMetadata());
        metadataShareEvent.addTrace(handler.getLocalCirrusId());

        Set<Host> joinedHosts = new HashSet<>();

        for(Host host : joiningHosts) {
            try {
                handler.getConnectionPool().addHost(host);
                ClientEventConnection connection = handler.getConnectionPool().fetchConnection(host);
                connection.sendEvent(hostShareEvent);
                connection.sendEvent(metadataShareEvent);

                joinedHosts.add(host);
            } catch (NetworkCirrusException e) {
                ActionFailureCirrusEvent failureEvent = new ActionFailureCirrusEvent();
                failureEvent.setException(e);
                failureEvent.setMessage(e.getMessage());
                try {
                    handler.accept(failureEvent);
                } catch (EventHandlerClosingCirrusException e1) {
                    e.printStackTrace();
                }
            }
        }

        HostCirrusEvent broadcastJoinedHosts = new HostCirrusEvent();
        broadcastJoinedHosts.addTrace(this.getTrace());
        broadcastJoinedHosts.addTrace(handler.getLocalCirrusId());

        CirrusEventPropagationStrategy propagationStrategy = new MetadataBroadcastPropagationStrategy();
        Set<Host> targets = propagationStrategy.getTargets(handler.getHostStorage(), broadcastJoinedHosts);

        for(Host host : targets){
            try {
                ClientEventConnection connection = handler.getConnectionPool().fetchConnection(host);
                connection.sendEvent(broadcastJoinedHosts);
            } catch (NetworkCirrusException e) {
                ActionFailureCirrusEvent failureEvent = new ActionFailureCirrusEvent();
                failureEvent.setException(e);
                failureEvent.setMessage(e.getMessage());
                try {
                    handler.accept(failureEvent);
                } catch (EventHandlerClosingCirrusException e1) {
                    e.printStackTrace();
                }
            }
        }

        handler.getHostStorage().updateHosts(joinedHosts);
    }
}
