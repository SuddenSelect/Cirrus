package pl.mmajewski.cirrus.network.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class HostCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> implements Serializable {
    private static final long serialVersionUID = 1681266000006L;

    private Set<Host> sharedHosts;

    public HostCirrusEvent(Set<Host> sharedHosts) {
        this.sharedHosts = sharedHosts;
    }

    @Override
    public  void event(ServerCirrusEventHandler handler){
        HostStorage storage = handler.getHostStorage();
        storage.updateHosts(sharedHosts);
    }
}
