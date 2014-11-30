package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;

import java.util.Collection;

/**
 * Strategy of propagating CirrusEvents.
 * Prevents positive feedback.
 * Minimizes duplicates.
 * Created by Maciej Majewski on 30/10/14.
 */
public interface CirrusEventPropagationStrategy {
    public <E extends CirrusEvent> void propagateEvent(Collection<? extends Host> knownHosts, E event);
}
