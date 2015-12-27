package pl.mmajewski.cirrus.main.coreevents.send;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.network.MetadataPropagationCirrusEvent;
import pl.mmajewski.cirrus.network.ConnectionPool;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;
import pl.mmajewski.cirrus.network.exception.NetworkCirrusException;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 21/11/15.
 */
public class SendSignupCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Host host = null;

    public void setHost(Host host) {
        this.host = host;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {
        SignupCirrusEvent signupEvent = new SignupCirrusEvent();
        Set<Host> joiningHosts = new HashSet<>();
        joiningHosts.add(handler.getHostStorage().fetchLocalHost());
        signupEvent.setJoiningHosts(joiningHosts);
        signupEvent.addTrace(handler.getLocalCirrusId());

        MetadataPropagationCirrusEvent propagationEvent = new MetadataPropagationCirrusEvent();
        propagationEvent.setMetadataSet(handler.getContentStorage().getAllContentMetadata());
        propagationEvent.addTrace(handler.getLocalCirrusId());

        ConnectionPool connectionPool = handler.getConnectionPool();
        connectionPool.addHost(host);
        try {
            ClientEventConnection connection = connectionPool.fetchConnection(host);
            connection.sendEvent(signupEvent);
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
