package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

import java.io.Serializable;
import java.util.Set;

/**
 * Strategy of propagating CirrusEvents.
 * Should prevent sending givent events to hosts who already had them.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface CirrusEventPropagationStrategy<E extends CirrusEvent> extends Serializable {
    public Set<Host> getTargets(HostStorage hostStorage, E event);
}
