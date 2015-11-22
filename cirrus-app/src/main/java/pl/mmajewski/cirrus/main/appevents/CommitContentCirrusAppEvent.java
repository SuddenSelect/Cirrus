package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.exception.EventCancelledCirrusException;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.coreevents.network.SendMetadataUpdateCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.storage.BalanceAndDiffuseStorageCirrusEvent;

import java.util.Set;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CommitContentCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    private boolean broadcastChange = true;

    public void setBroadcastChange(boolean broadcastChange){
        this.broadcastChange = broadcastChange;
    }

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        BalanceAndDiffuseStorageCirrusEvent evt = new BalanceAndDiffuseStorageCirrusEvent();
        evt.init();

        ContentStorage prepared = handler.getContentStorage();
        evt.setStorageToCommit(prepared);

        CirrusEventHandler coreHandler = handler.getCoreEventHandler();
        try {
            coreHandler.accept(evt);
        } catch (EventHandlerClosingCirrusException e) {
            throw new EventCancelledCirrusException(e);
        }

        Set<ContentMetadata> newContentMetadata = prepared.getAllContentMetadata();
        handler.resetStorage();


        if(broadcastChange) {
            SendMetadataUpdateCirrusEvent updateEvent = new SendMetadataUpdateCirrusEvent();
            updateEvent.setMetadataSet(newContentMetadata);
            try {
                coreHandler.accept(updateEvent);
            } catch (EventHandlerClosingCirrusException e) {
                throw new EventCancelledCirrusException(e);
            }
        }
    }
}
