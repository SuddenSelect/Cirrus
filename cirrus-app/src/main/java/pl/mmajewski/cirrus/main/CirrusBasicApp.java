package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEventHandler;
import pl.mmajewski.cirrus.impl.persistance.DiskContentStorage;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusBasicApp  {
    private ContentStorage prepared = new DiskContentStorage();
    private CirrusCore core = new CirrusCore();
    private AppEventHandler appEventHandler = this.new AppEventHandler();

    public CirrusBasicApp(){
        core.getCoreEventHandler().setAppEventHandler(appEventHandler);
    }


    private void resetPreparedContentStorage(){
        prepared = new DiskContentStorage();
    }

    public class AppEventHandler implements CirrusAppEventHandler {
        @Override
        public void setContentStorage(ContentStorage contentStorage) {
            throw new UnsupportedOperationException();
        }

        public ContentStorage getContentStorage() {
            return CirrusBasicApp.this.prepared;
        }

        public void resetStorage(){
            CirrusBasicApp.this.resetPreparedContentStorage();
        }

        public CirrusEventHandler getCoreEventHandler(){
            return core.getCoreEventHandler();
        }
    }

}
