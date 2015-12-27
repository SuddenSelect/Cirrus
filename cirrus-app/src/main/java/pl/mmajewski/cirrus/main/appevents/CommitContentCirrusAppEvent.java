package pl.mmajewski.cirrus.main.appevents;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.event.CirrusAppEvent;
import pl.mmajewski.cirrus.exception.EventCancelledCirrusException;
import pl.mmajewski.cirrus.impl.client.BroadcastPropagationStrategy;
import pl.mmajewski.cirrus.impl.client.EqualDiffusionStrategy;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.coreevents.send.SendAvailabilityPropagationCirrusEvent;
import pl.mmajewski.cirrus.main.coreevents.storage.BalanceAndDiffuseStorageCirrusEvent;

import java.io.IOException;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CommitContentCirrusAppEvent extends CirrusAppEvent<CirrusBasicApp.AppEventHandler> {

    private boolean broadcastChange = true;
    private int diffusionRedundancy = 1;

    public void setBroadcastChange(boolean broadcastChange){
        this.broadcastChange = broadcastChange;
    }

    public void setDiffusionRedundancy(int diffusionRedundancy) {
        this.diffusionRedundancy = diffusionRedundancy;
    }

    @Override
    public void appEvent(CirrusBasicApp.AppEventHandler handler) {
        ContentStorage prepared = handler.getContentStorage();
        CirrusEventHandler coreHandler = handler.getCoreEventHandler();

        if(!broadcastChange) {
            ContentStorage storage = coreHandler.getContentStorage();
            storage.updateContentMetadata(prepared.getAllContentMetadata());
            for (ContentMetadata metadata : prepared.getAllContentMetadata()) {
                for (ContentPiece piece : prepared.getAvailablePieces(metadata)) {
                    try {
                        storage.storeContentPiece(piece);
                    } catch (IOException e) {
                        throw new EventCancelledCirrusException(e);
                    }
                }
            }
        }else {
            BalanceAndDiffuseStorageCirrusEvent evt = new BalanceAndDiffuseStorageCirrusEvent();
            evt.setStorageToCommit(prepared);
            evt.setDiffusionStrategy(new EqualDiffusionStrategy().setRedundancy(diffusionRedundancy));
            evt.setAvailabilityPropagationStrategy(new BroadcastPropagationStrategy<SendAvailabilityPropagationCirrusEvent>());
            try {
                coreHandler.accept(evt);
            } catch (EventHandlerClosingCirrusException e) {
                throw new EventCancelledCirrusException(e);
            }
        }

//        Set<ContentMetadata> newContentMetadata = prepared.getAllContentMetadata();
        handler.resetStorage();


//        if(broadcastChange) {
//            SendMetadataUpdateCirrusEvent updateEvent = new SendMetadataUpdateCirrusEvent();
//            updateEvent.setMetadataSet(newContentMetadata);
//            try {
//                coreHandler.accept(updateEvent);
//            } catch (EventHandlerClosingCirrusException e) {
//                throw new EventCancelledCirrusException(e);
//            }
//        }
    }
}
