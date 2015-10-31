package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.content.ContentAccessor;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.exception.EventCancelledCirrusException;
import pl.mmajewski.cirrus.impl.content.accessors.ContentAccessorImplPlainBQueue;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Maciej Majewski on 2015-02-06.
 */
public class DumpFileCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> implements Serializable {
    private static final long serialVersionUID = 1681266000010L;

    private static Logger logger = Logger.getLogger(DumpFileCirrusAppEvent.class.getName());

    private ContentMetadata metadata;
    private String file;

    public DumpFileCirrusAppEvent(ContentMetadata metadata, String file) {
        this.metadata = metadata;
        this.file = file;
    }

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        ContentAccessor fileDumper = new ContentAccessorImplPlainBQueue(metadata,handler.getCoreEventHandler());
        try {
            fileDumper.saveAsFile(file);
        } catch (EventHandlerClosingCirrusException|FileNotFoundException e) {
            logger.warning(e.getMessage());
            throw new EventCancelledCirrusException(e);
        }
    }
}
