package pl.mmajewski.cirrus.event;

import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.impl.persistance.DiskContentStorage;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class SimpleCirrusAppEventHandler implements CirrusAppEventHandler {
    private ContentStorage prepared = new DiskContentStorage();

    @Override
    public void setContentStorage(ContentStorage contentStorage) {
        throw new UnsupportedOperationException();
    }

    public ContentStorage getContentStorage() {
        return prepared;
    }

    public void resetStorage(){
        prepared = new DiskContentStorage();
    }
}
