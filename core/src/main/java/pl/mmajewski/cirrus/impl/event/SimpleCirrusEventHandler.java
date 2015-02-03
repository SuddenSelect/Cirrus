package pl.mmajewski.cirrus.impl.event;

import pl.mmajewski.cirrus.binding.common.Persistance;
import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class SimpleCirrusEventHandler implements CirrusEventHandler {
    private CirrusEventHandler appEventHandler;

    private ContentStorage contentStorage;

    @Override
    public void setAppEventHandler(CirrusEventHandler handler) {
        appEventHandler = handler;
    }

    @Override
    public CirrusEventHandler getAppEventHandler() {
        return appEventHandler;
    }

    @Override
    public void setContentStorage(ContentStorage contentStorage) {
        this.contentStorage = contentStorage;
    }

    public ContentStorage getContentStorage() {
        return contentStorage;
    }

}
