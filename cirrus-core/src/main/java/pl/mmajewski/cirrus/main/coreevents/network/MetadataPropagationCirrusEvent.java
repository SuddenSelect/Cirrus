package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.Set;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class MetadataPropagationCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private Set<ContentMetadata> metadataSet;

    public void setMetadataList(Set<ContentMetadata> metadataSet) {
        this.metadataSet = metadataSet;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage contentStorage = handler.getContentStorage();
        contentStorage.updateContentMetadata(metadataSet);
    }
}
