package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class HostUpdateCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<Host> sharedHosts;

    public void setSharedHosts(Set<Host> sharedHosts) {
        this.sharedHosts = sharedHosts;
    }

    @Override
    public  void event(ServerCirrusEventHandler handler) {
        for(Host host : sharedHosts){
            host.simulateFieldTransiency();
        }
        HostStorage storage = handler.getHostStorage();
        storage.updateHosts(sharedHosts);
    }
}
