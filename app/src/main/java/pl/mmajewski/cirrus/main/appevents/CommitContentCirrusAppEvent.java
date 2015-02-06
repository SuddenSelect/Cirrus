package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.coreevents.storage.CommitStorageCirrusEvent;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CommitContentCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        CommitStorageCirrusEvent evt = new CommitStorageCirrusEvent();
        evt.init();

        ContentStorage prepared = handler.getContentStorage();
        evt.setStorageToCommit(prepared);

        CirrusEventHandler coreHandler = handler.getCoreEventHandler();
        coreHandler.accept(evt);

        handler.resetStorage();
    }
}
