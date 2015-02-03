package pl.mmajewski.cirrus.appevents;

import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.event.SimpleCirrusAppEventHandler;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class PrepareFileCirrusAppEvent extends CirrusAppEvent<SimpleCirrusAppEventHandler> {

    private String file;

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public void appEvent(SimpleCirrusAppEventHandler handler) {
        try {
            ContentAdapter adapter = new ContentAdapterImplPlainFile();
            adapter.adapt(file);//generates NewContentPrepared...
        } catch (ContentAdapterCirrusException e) {
            ActionFailureCirrusAppEvent evt = new ActionFailureCirrusAppEvent();
            evt.init();
            evt.setException(e);
            evt.setMessage(e.getMessage());
            handler.accept(evt);
        }
    }
}