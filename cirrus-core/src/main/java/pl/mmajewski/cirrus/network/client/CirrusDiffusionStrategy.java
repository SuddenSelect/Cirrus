package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public interface CirrusDiffusionStrategy<E extends CirrusEvent> extends Serializable {
    public Map<Host, E> getTargets(ContentStorage storage, HostStorage hostStorage);
}
