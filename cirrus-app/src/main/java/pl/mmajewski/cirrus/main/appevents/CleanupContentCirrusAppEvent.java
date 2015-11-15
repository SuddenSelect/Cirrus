package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CleanupContentCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> implements Serializable {
    private static final long serialVersionUID = 1681266000012L;

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        ContentStorage storage = handler.getContentStorage();
        Set<ContentMetadata> toDel = new HashSet<>(storage.getAllContentMetadata());
        for(ContentMetadata meta : toDel){
            storage.deleteContent(meta);
        }
        handler.resetStorage();
    }
}
