package pl.mmajewski.cirrus.impl.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.main.coreevents.network.MetadataPropagationCirrusEvent;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;

import java.util.Set;

/**
 * Created by Maciej Majewski on 22/11/15.
 */
public class BroadcastPropagationStrategy<E extends CirrusEvent> implements CirrusEventPropagationStrategy<E> {

        @Override
        public Set<Host> getTargets(HostStorage hostStorage, E event) {
            Set<Host> targets = hostStorage.fetchAllHosts();
            targets.removeIf(host -> event.getTrace().contains(host.getCirrusId()));
            targets.removeIf(host -> host.equals(hostStorage.fetchLocalHost()));
            return targets;
        }

}
