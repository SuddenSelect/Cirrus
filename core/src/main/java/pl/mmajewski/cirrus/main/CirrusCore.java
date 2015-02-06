package pl.mmajewski.cirrus.main;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusCore {
    private ContentStorage contentStorage;
    private CoreEventHandler coreEventHandler = this.new CoreEventHandler();




    public CirrusEventHandler getCoreEventHandler(){
        return coreEventHandler;
    }

    public class CoreEventHandler implements CirrusEventHandler{
        private CirrusEventHandler appEventHandler;

        @Override
        public void setAppEventHandler(CirrusEventHandler handler) {
            this.appEventHandler = handler;
        }

        @Override
        public CirrusEventHandler getAppEventHandler() {
            return appEventHandler;
        }

        @Override
        public void setContentStorage(ContentStorage contentStorage) {
            CirrusCore.this.contentStorage = contentStorage;
        }

        public ContentStorage getContentStorage() {
            return CirrusCore.this.contentStorage;
        }
    }
}
