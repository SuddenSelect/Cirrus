package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CleanupContentCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        for(ContentMetadata meta : storage.getAllContentMetadata()){
            storage.deleteContent(meta);
        }
        handler.resetStorage();
    }
}