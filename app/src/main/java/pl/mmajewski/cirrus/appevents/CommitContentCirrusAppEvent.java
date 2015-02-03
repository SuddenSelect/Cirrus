package pl.mmajewski.cirrus.appevents;

import pl.mmajewski.cirrus.binding.common.EventHandler;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.coreevents.storage.CommitStorageCirrusEvent;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.event.SimpleCirrusAppEventHandler;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CommitContentCirrusAppEvent extends CirrusAppEvent<SimpleCirrusAppEventHandler> {

    @Override
    public void appEvent(SimpleCirrusAppEventHandler handler) {
        CommitStorageCirrusEvent evt = new CommitStorageCirrusEvent();
        evt.init();

        ContentStorage prepared = handler.getContentStorage();
        evt.setStorageToCommit(prepared);

        CirrusEventHandler coreHandler = EventHandler.getInstance();
        coreHandler.accept(evt);

        handler.resetStorage();
    }
}
