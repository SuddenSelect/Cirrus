package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.exception.EventCancelledCirrusException;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class AdaptFileCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    private String file;

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        try {
            ContentAdapter adapter = new ContentAdapterImplPlainFile();
            adapter.adapt(file);//generates NewContentPrepared...
        } catch (ContentAdapterCirrusException e) {
            ActionFailureCirrusAppEvent evt = new ActionFailureCirrusAppEvent();
            evt.init();
            evt.setException(e);
            evt.setMessage(e.getMessage());
            try {
                handler.accept(evt);
            } catch (EventHandlerClosingCirrusException e1) {
                throw new EventCancelledCirrusException(e1);
            }
        }
    }
}
