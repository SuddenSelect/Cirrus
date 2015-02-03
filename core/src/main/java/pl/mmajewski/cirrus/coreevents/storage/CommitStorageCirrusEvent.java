package pl.mmajewski.cirrus.coreevents.storage;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.impl.event.SimpleCirrusEventHandler;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CommitStorageCirrusEvent extends CirrusEvent<SimpleCirrusEventHandler> {

    private ContentStorage toCommit;

    public void setStorageToCommit(ContentStorage toCommit) {
        this.toCommit = toCommit;
    }

    @Override
    public void event(SimpleCirrusEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        //TODO push new content to memory storage
        //TODO generate events for content updates
        //TODO initiate content diffusion
    }
}
