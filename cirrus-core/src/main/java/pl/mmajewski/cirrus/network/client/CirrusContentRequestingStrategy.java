package pl.mmajewski.cirrus.network.client;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

import java.util.Map;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public interface CirrusContentRequestingStrategy<E extends CirrusEvent> {
    public Map<Host, E> getTargets(HostStorage hostStorage,
                                   ContentStorage contentStorage,
                                   ContentMetadata contentMetadata);
}
