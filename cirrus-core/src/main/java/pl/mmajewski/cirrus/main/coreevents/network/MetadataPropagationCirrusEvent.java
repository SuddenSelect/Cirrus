package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;
import pl.mmajewski.cirrus.main.coreevents.ActionFailureCirrusEvent;

import java.util.Set;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class MetadataPropagationCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private Set<ContentMetadata> metadataSet;

    public void setMetadataSet(Set<ContentMetadata> metadataSet) {
        this.metadataSet = metadataSet;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage contentStorage = handler.getContentStorage();
        contentStorage.updateContentMetadata(metadataSet);

        SendMetadataUpdateCirrusEvent updateEvent = new SendMetadataUpdateCirrusEvent();
        updateEvent.addTrace(this.getTrace());
        updateEvent.setMetadataSet(metadataSet);

        try {
            handler.accept(updateEvent);
        } catch (EventHandlerClosingCirrusException e) {
            ActionFailureCirrusEvent failureEvent = new ActionFailureCirrusEvent();
            failureEvent.setException(e);
            failureEvent.setMessage(e.getMessage());
            e.printStackTrace();
            try {
                handler.accept(failureEvent);
            } catch (EventHandlerClosingCirrusException e1) {
                e.printStackTrace();
            }
        }
    }
}
