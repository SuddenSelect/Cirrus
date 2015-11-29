package pl.mmajewski.cirrus.network.event;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 09/11/14.
 */
public class FetchContentCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Set<ContentMetadata> sharedMetadata;

    public void setSharedMetadata(Set<ContentMetadata> sharedMetadata) {
        this.sharedMetadata = sharedMetadata;
    }

    @Override
    public void event(ServerCirrusEventHandler handler){
        ContentStorage storage = handler.getContentStorage();
        storage.updateContentMetadata(sharedMetadata);
    }
}
