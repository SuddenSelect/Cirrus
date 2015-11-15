package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.main.CirrusCoreEventHandler;

import java.util.List;

/**
 * Created by Maciej Majewski on 15/11/15.
 */
public class DeleteContentCirrusEvent extends CirrusEvent<CirrusCoreEventHandler> {

    private List<ContentMetadata> contentMetadataList;

    public void setContentMetadataList(List<ContentMetadata> contentMetadataList) {
        this.contentMetadataList = contentMetadataList;
    }

    @Override
    public void event(CirrusCoreEventHandler handler) {
        ContentStorage contentStorage = handler.getContentStorage();
        for(ContentMetadata contentMetadata : contentMetadataList) {
            contentStorage.deleteContent(contentMetadata);
        }
    }
}
