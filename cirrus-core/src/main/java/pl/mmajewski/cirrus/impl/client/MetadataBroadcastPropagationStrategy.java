package pl.mmajewski.cirrus.impl.client;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.main.coreevents.network.MetadataPropagationCirrusEvent;
import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;

import java.util.Set;

/**
 * Created by Maciej Majewski on 22/11/15.
 */
public class MetadataBroadcastPropagationStrategy implements CirrusEventPropagationStrategy<MetadataPropagationCirrusEvent> {

        @Override
        public Set<Host> getTargets(HostStorage hostStorage, MetadataPropagationCirrusEvent event) {
            Set<Host> targets = hostStorage.fetchAllHosts();
            targets.removeIf(host -> event.getTrace().contains(host.getCirrusId()));
            targets.removeIf(host -> host.equals(hostStorage.fetchLocalHost()));
            return targets;
        }

}
